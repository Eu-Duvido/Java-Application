-- ─────────────────────────────────────────────────────────────────────────────
-- V2: Dashboard Analytics Views
-- Consulta apenas tabelas da aplicação (users, challenges, challenge_participations, proofs)
-- Pontos = provas aprovadas (1 prova aprovada = 1 ponto)
-- ─────────────────────────────────────────────────────────────────────────────

CREATE OR REPLACE VIEW vw_user_ranking AS
SELECT
    u.id                                                                    AS user_id,
    u.name                                                                  AS username,
    u.profile_image_url                                                     AS avatar_url,
    COALESCE(SUM(CASE WHEN pr.approved = TRUE THEN 1 ELSE 0 END), 0)       AS total_points,
    ROW_NUMBER() OVER (
        ORDER BY COALESCE(SUM(CASE WHEN pr.approved = TRUE THEN 1 ELSE 0 END), 0) DESC
    )                                                                       AS ranking_position
FROM users u
LEFT JOIN challenge_participations cp ON cp.user_id = u.id
LEFT JOIN proofs pr                   ON pr.participation_id = cp.id
GROUP BY u.id, u.name, u.profile_image_url;

-- ─────────────────────────────────────────────────────────────────────────────

CREATE OR REPLACE VIEW vw_daily_points AS
SELECT
    DATE(pr.submitted_at)                                                   AS date,
    cp.user_id,
    cp.challenge_id,
    SUM(CASE WHEN pr.approved = TRUE THEN 1 ELSE 0 END)                    AS points
FROM proofs pr
JOIN challenge_participations cp ON cp.id = pr.participation_id
GROUP BY DATE(pr.submitted_at), cp.user_id, cp.challenge_id;

-- ─────────────────────────────────────────────────────────────────────────────

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
    COUNT(CASE WHEN pr.approved = TRUE THEN pr.id END)                     AS approved_proofs,
    ROUND(COALESCE(AVG(cp.progress), 0), 2)                                AS avg_progress
FROM challenges c
LEFT JOIN challenge_participations cp ON cp.challenge_id = c.id
LEFT JOIN proofs pr                   ON pr.participation_id = cp.id
GROUP BY c.id, c.title, c.difficulty, c.subject, c.status;

-- ─────────────────────────────────────────────────────────────────────────────
-- Linha única (sem GROUP BY) — sempre retorna exatamente 1 registro

CREATE OR REPLACE VIEW vw_evidence_metrics AS
SELECT
    COUNT(CASE WHEN pr.approved = TRUE                                    THEN 1 END) AS approved_count,
    COUNT(CASE WHEN pr.approved = FALSE AND pr.rejection_reason IS NOT NULL THEN 1 END) AS rejected_count,
    COUNT(CASE WHEN pr.approved = FALSE AND pr.rejection_reason IS NULL   THEN 1 END) AS pending_count,
    COUNT(*)                                                                            AS total_count,
    ROUND(
        COALESCE(
            100.0 * COUNT(CASE WHEN pr.approved = TRUE THEN 1 END) / NULLIF(COUNT(*), 0),
            0
        ), 2
    )                                                                                   AS approval_rate
FROM proofs pr;
