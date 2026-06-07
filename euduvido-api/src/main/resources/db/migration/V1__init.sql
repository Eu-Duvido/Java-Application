CREATE TABLE users
(
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    name              VARCHAR(100) NOT NULL,
    email             VARCHAR(150) NOT NULL,
    password          VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE challenges
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
    CONSTRAINT fk_challenges_creator FOREIGN KEY (creator_id) REFERENCES users (id)
);

CREATE TABLE challenge_participants
(
    challenge_id BIGINT NOT NULL,
    user_id      BIGINT NOT NULL,
    PRIMARY KEY (challenge_id, user_id),
    CONSTRAINT fk_cp_challenge FOREIGN KEY (challenge_id) REFERENCES challenges (id),
    CONSTRAINT fk_cp_user      FOREIGN KEY (user_id)      REFERENCES users (id)
);

CREATE TABLE challenge_participations
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

CREATE TABLE proofs
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
    CONSTRAINT fk_proofs_participation FOREIGN KEY (participation_id) REFERENCES challenge_participations (id)
);

CREATE TABLE ai_insights
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
    dt_geracao      DATETIME      NOT NULL,
    PRIMARY KEY (id_insight),
    INDEX ix_ai_perfil(no_curso, no_regiao, tp_modalidade),
    INDEX ix_ai_tipo(tipo),
    INDEX ix_ai_nivel(nivel),
    INDEX ix_ai_dt_geracao(dt_geracao)
);

