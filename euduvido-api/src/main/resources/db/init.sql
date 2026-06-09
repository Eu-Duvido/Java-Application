-- Consolidated database bootstrap for the Eu Duvido backend.
-- Use this file to initialize a fresh MySQL database with all structures
-- required by the application endpoints.

CREATE TABLE IF NOT EXISTS users
(
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    name              VARCHAR(100) NOT NULL,
    email             VARCHAR(150) NOT NULL,
    password          VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS challenges
(
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    title             VARCHAR(150) NOT NULL,
    description       TEXT         NOT NULL,
    difficulty        VARCHAR(10),
    subject           VARCHAR(100),
    goal_type         VARCHAR(20),
    goal_value        INT,
    deadline          DATETIME     NOT NULL,
    status            VARCHAR(50)  NOT NULL,
    location_required BOOLEAN,
    created_at        DATETIME     NOT NULL,
    creator_id        BIGINT       NOT NULL,
    PRIMARY KEY (id),
    INDEX ix_challenges_creator(creator_id),
    CONSTRAINT fk_challenges_creator FOREIGN KEY (creator_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS challenge_participants
(
    challenge_id BIGINT NOT NULL,
    user_id      BIGINT NOT NULL,
    PRIMARY KEY (challenge_id, user_id),
    CONSTRAINT fk_cp_challenge FOREIGN KEY (challenge_id) REFERENCES challenges (id),
    CONSTRAINT fk_cp_user      FOREIGN KEY (user_id)      REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS challenge_participations
(
    id           BIGINT      NOT NULL AUTO_INCREMENT,
    user_id      BIGINT      NOT NULL,
    challenge_id BIGINT      NOT NULL,
    status       VARCHAR(50) NOT NULL,
    progress     INT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uq_participation             UNIQUE (user_id, challenge_id),
    CONSTRAINT fk_participation_user        FOREIGN KEY (user_id)      REFERENCES users (id),
    CONSTRAINT fk_participation_challenge   FOREIGN KEY (challenge_id) REFERENCES challenges (id)
);

CREATE TABLE IF NOT EXISTS proofs
(
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    participation_id BIGINT       NOT NULL,
    media_url        VARCHAR(255) NOT NULL,
    media_type       VARCHAR(50)  NOT NULL,
    latitude         DOUBLE,
    longitude        DOUBLE,
    submitted_at     DATETIME     NOT NULL,
    approved         BOOLEAN      NOT NULL DEFAULT FALSE,
    rejection_reason TEXT,
    ai_valid         BOOLEAN,
    ai_confidence    DOUBLE,
    ai_reason        TEXT,
    PRIMARY KEY (id),
    INDEX ix_proofs_participation(participation_id),
    CONSTRAINT fk_proofs_participation FOREIGN KEY (participation_id) REFERENCES challenge_participations (id)
);

CREATE TABLE IF NOT EXISTS ai_insights
(
    id_insight      BIGINT        NOT NULL AUTO_INCREMENT,
    no_curso        VARCHAR(255)  NOT NULL,
    no_area_geral   VARCHAR(255)  NOT NULL,
    no_regiao       VARCHAR(50)   NOT NULL,
    tp_modalidade   INT           NOT NULL,
    nu_ano_censo    INT,
    tipo            VARCHAR(50)   NOT NULL,
    titulo          VARCHAR(255)  NOT NULL,
    descricao       TEXT          NOT NULL,
    valor_destaque  DOUBLE        NOT NULL,
    unidade         VARCHAR(100)  NOT NULL,
    interpretacao   TEXT          NOT NULL,
    dados_grafico   JSON          NOT NULL,
    nivel           VARCHAR(10)   NOT NULL,
    fonte           VARCHAR(20)   NOT NULL DEFAULT 'gemini',
    dt_geracao      DATETIME      NOT NULL,
    PRIMARY KEY (id_insight),
    INDEX ix_ai_perfil(no_curso, no_regiao, tp_modalidade),
    INDEX ix_ai_tipo(tipo),
    INDEX ix_ai_nivel(nivel),
    INDEX ix_ai_dt_geracao(dt_geracao)
);

-- Dashboard views backed by the application tables.

CREATE OR REPLACE VIEW vw_user_ranking AS
SELECT
    u.id                                                              AS user_id,
    u.name                                                            AS username,
    u.profile_image_url                                               AS avatar_url,
    COALESCE(SUM(CASE WHEN pr.approved = TRUE THEN 1 ELSE 0 END), 0) AS total_points,
    ROW_NUMBER() OVER (
        ORDER BY COALESCE(SUM(CASE WHEN pr.approved = TRUE THEN 1 ELSE 0 END), 0) DESC
    )                                                                 AS ranking_position
FROM users u
LEFT JOIN challenge_participations cp ON cp.user_id = u.id
LEFT JOIN proofs pr                   ON pr.participation_id = cp.id
GROUP BY u.id, u.name, u.profile_image_url;

CREATE OR REPLACE VIEW vw_daily_points AS
SELECT
    DATE(pr.submitted_at)                                AS date,
    cp.user_id,
    cp.challenge_id,
    SUM(CASE WHEN pr.approved = TRUE THEN 1 ELSE 0 END) AS points
FROM proofs pr
JOIN challenge_participations cp ON cp.id = pr.participation_id
GROUP BY DATE(pr.submitted_at), cp.user_id, cp.challenge_id;

CREATE OR REPLACE VIEW vw_challenge_metrics AS
SELECT
    c.id                                                                    AS challenge_id,
    c.title,
    c.difficulty,
    c.subject,
    c.status,
    COUNT(DISTINCT cp.user_id)                                              AS participant_count,
    COUNT(DISTINCT CASE WHEN cp.status = 'COMPLETED'   THEN cp.user_id END) AS completed_count,
    COUNT(DISTINCT CASE WHEN cp.status = 'IN_PROGRESS' THEN cp.user_id END) AS in_progress_count,
    COUNT(pr.id)                                                            AS total_proofs,
    COUNT(CASE WHEN pr.approved = TRUE THEN pr.id END)                      AS approved_proofs,
    ROUND(COALESCE(AVG(cp.progress), 0), 2)                                  AS avg_progress
FROM challenges c
LEFT JOIN challenge_participations cp ON cp.challenge_id = c.id
LEFT JOIN proofs pr                   ON pr.participation_id = cp.id
GROUP BY c.id, c.title, c.difficulty, c.subject, c.status;

CREATE OR REPLACE VIEW vw_evidence_metrics AS
SELECT
    COUNT(CASE WHEN pr.approved = TRUE                                      THEN 1 END) AS approved_count,
    COUNT(CASE WHEN pr.approved = FALSE AND pr.rejection_reason IS NOT NULL THEN 1 END) AS rejected_count,
    COUNT(CASE WHEN pr.approved = FALSE AND pr.rejection_reason IS NULL     THEN 1 END) AS pending_count,
    COUNT(*)                                                                            AS total_count,
    ROUND(
        COALESCE(
            100.0 * COUNT(CASE WHEN pr.approved = TRUE THEN 1 END) / NULLIF(COUNT(*), 0),
            0
        ), 2
    )                                                                                   AS approval_rate
FROM proofs pr;

-- INEP compatibility structures.
-- The data-pipeline can populate these structures, or replace them with real views
-- in environments where the analytical pipeline owns the vw_* objects.

CREATE TABLE IF NOT EXISTS vw_resumo_geral
(
    nu_ano_censo          INT,
    total_ies             BIGINT,
    total_cursos          BIGINT,
    total_vagas           BIGINT,
    total_ingressantes    BIGINT,
    total_matriculados    BIGINT,
    total_concluintes     BIGINT,
    pct_ing_feminino      DOUBLE,
    pct_ing_masculino     DOUBLE,
    taxa_conclusao_pct    DOUBLE,
    total_ing_via_enem    BIGINT,
    pct_enem              DOUBLE,
    INDEX ix_vw_resumo_ano(nu_ano_censo)
);

CREATE TABLE IF NOT EXISTS vw_ranking_cursos_regiao
(
    no_regiao             VARCHAR(50),
    no_curso              VARCHAR(255),
    no_area_geral         VARCHAR(255),
    total_ingressantes    BIGINT,
    total_matriculados    BIGINT,
    total_concluintes     BIGINT,
    total_vagas           BIGINT,
    pct_conclusao         DOUBLE,
    pct_ocupacao_vagas    DOUBLE,
    rank_na_regiao        BIGINT,
    INDEX ix_vw_rank_regiao(no_curso, no_regiao),
    INDEX ix_vw_rank_pos(no_regiao, rank_na_regiao)
);

CREATE TABLE IF NOT EXISTS vw_ranking_cursos_area
(
    no_curso              VARCHAR(255),
    no_area_geral         VARCHAR(255),
    no_area_especifica    VARCHAR(255),
    total_ingressantes    BIGINT,
    total_vagas           BIGINT,
    total_matriculados    BIGINT,
    total_concluintes     BIGINT,
    num_ies_ofertantes    BIGINT,
    pct_conclusao         DOUBLE,
    INDEX ix_vw_rank_area_curso(no_curso),
    INDEX ix_vw_rank_area_geral(no_area_geral)
);

CREATE TABLE IF NOT EXISTS vw_demografico_genero_ingressantes
(
    nu_ano_censo          INT,
    total_ingressantes    BIGINT,
    feminino              BIGINT,
    masculino             BIGINT,
    INDEX ix_vw_genero_ano(nu_ano_censo)
);

CREATE TABLE IF NOT EXISTS vw_demografico_raca_ingressantes
(
    total_ingressantes    BIGINT,
    branca                BIGINT,
    preta                 BIGINT,
    parda                 BIGINT,
    amarela               BIGINT,
    indigena              BIGINT,
    cor_nao_declarada     BIGINT
);

CREATE TABLE IF NOT EXISTS vw_demografico_etaria_ingressantes
(
    total_ingressantes    BIGINT,
    faixa_0_17            BIGINT,
    faixa_18_24           BIGINT,
    faixa_25_29           BIGINT,
    faixa_30_34           BIGINT,
    faixa_35_39           BIGINT,
    faixa_40_49           BIGINT,
    faixa_50_59           BIGINT,
    faixa_60_mais         BIGINT
);

CREATE TABLE IF NOT EXISTS vw_ead_vs_presencial
(
    no_regiao             VARCHAR(50),
    modalidade            VARCHAR(30),
    total_matriculados    BIGINT,
    total_ingressantes    BIGINT,
    total_concluintes     BIGINT,
    total_vagas           BIGINT,
    num_cursos            BIGINT,
    INDEX ix_vw_ead_regiao(no_regiao),
    INDEX ix_vw_ead_modalidade(modalidade)
);

CREATE TABLE IF NOT EXISTS vw_taxa_conclusao_ies
(
    no_ies                VARCHAR(255),
    sg_ies                VARCHAR(50),
    no_regiao             VARCHAR(50),
    sg_uf                 VARCHAR(2),
    total_matriculados    BIGINT,
    total_concluintes     BIGINT,
    trancamentos          BIGINT,
    desvinculados         BIGINT,
    INDEX ix_vw_taxa_ies(no_ies),
    INDEX ix_vw_taxa_regiao(no_regiao)
);
