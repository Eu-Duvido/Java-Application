-- ─────────────────────────────────────────────────────────────────────────────
-- V3: Recria as views da dashboard garantindo os aliases corretos
-- Necessário porque Flyway não reaplica V2 já executado.
-- Aliases obrigatórios para compatibilidade com DashboardRepository:
--   vw_user_ranking   → user_id, username, avatar_url, total_points, ranking_position
--   vw_daily_points   → date, user_id, challenge_id, points
--   vw_challenge_metrics → challenge_id, title, difficulty, subject, status,
--                          participant_count, completed_count, in_progress_count,
--                          total_proofs, approved_proofs, avg_progress
--   vw_evidence_metrics  → approved_count, rejected_count, pending_count,
--                          total_count, approval_rate
-- ─────────────────────────────────────────────────────────────────────────────

DROP VIEW IF EXISTS vw_user_ranking;
DROP VIEW IF EXISTS vw_daily_points;
DROP VIEW IF EXISTS vw_challenge_metrics;
DROP VIEW IF EXISTS vw_evidence_metrics;

-- ─────────────────────────────────────────────────────────────────────────────
-- Ranking de usuários por pontos (provas aprovadas)
-- users.name       → alias username
-- users.profile_image_url → alias avatar_url
-- ─────────────────────────────────────────────────────────────────────────────
CREATE VIEW vw_user_ranking AS
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
-- Pontos diários por usuário e desafio
-- ─────────────────────────────────────────────────────────────────────────────
CREATE VIEW vw_daily_points AS
SELECT
    DATE(pr.submitted_at)                                                   AS date,
    cp.user_id,
    cp.challenge_id,
    SUM(CASE WHEN pr.approved = TRUE THEN 1 ELSE 0 END)                    AS points
FROM proofs pr
JOIN challenge_participations cp ON cp.id = pr.participation_id
GROUP BY DATE(pr.submitted_at), cp.user_id, cp.challenge_id;

-- ─────────────────────────────────────────────────────────────────────────────
-- Métricas por desafio
-- ─────────────────────────────────────────────────────────────────────────────
CREATE VIEW vw_challenge_metrics AS
SELECT
    c.id                                                                     AS challenge_id,
    c.title,
    c.difficulty,
    c.subject,
    c.status,
    COUNT(DISTINCT cp.user_id)                                               AS participant_count,
    COUNT(DISTINCT CASE WHEN cp.status = 'COMPLETED'   THEN cp.user_id END) AS completed_count,
    COUNT(DISTINCT CASE WHEN cp.status = 'IN_PROGRESS' THEN cp.user_id END) AS in_progress_count,
    COUNT(pr.id)                                                             AS total_proofs,
    COUNT(CASE WHEN pr.approved = TRUE THEN pr.id END)                      AS approved_proofs,
    ROUND(COALESCE(AVG(cp.progress), 0), 2)                                 AS avg_progress
FROM challenges c
LEFT JOIN challenge_participations cp ON cp.challenge_id = c.id
LEFT JOIN proofs pr                   ON pr.participation_id = cp.id
GROUP BY c.id, c.title, c.difficulty, c.subject, c.status;

-- ─────────────────────────────────────────────────────────────────────────────
-- Métricas globais de evidências (sempre 1 registro)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE VIEW vw_evidence_metrics AS
SELECT
    COUNT(CASE WHEN pr.approved = TRUE                                       THEN 1 END) AS approved_count,
    COUNT(CASE WHEN pr.approved = FALSE AND pr.rejection_reason IS NOT NULL  THEN 1 END) AS rejected_count,
    COUNT(CASE WHEN pr.approved = FALSE AND pr.rejection_reason IS NULL      THEN 1 END) AS pending_count,
    COUNT(*)                                                                              AS total_count,
    ROUND(
        COALESCE(
            100.0 * COUNT(CASE WHEN pr.approved = TRUE THEN 1 END) / NULLIF(COUNT(*), 0),
            0
        ), 2
    )                                                                                     AS approval_rate
FROM proofs pr;
