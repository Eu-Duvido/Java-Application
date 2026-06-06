package com.euduvido.euduvido_api.infrastructure.dashboard;

import com.euduvido.euduvido_api.entrypoint.dtos.response.ChallengeMetricsResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.DailyPointsResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.EngagementMetricsResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.EvidenceMetricsResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.UserRankingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class DashboardRepository {

    private static final Logger log = LoggerFactory.getLogger(DashboardRepository.class);

    private final JdbcTemplate jdbc;

    public DashboardRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<UserRankingResponse> findUserRanking() {
        return jdbc.query(
                "SELECT user_id, username, avatar_url, total_points, ranking_position" +
                " FROM vw_user_ranking ORDER BY ranking_position ASC",
                (rs, rowNum) -> new UserRankingResponse(
                        rs.getLong("user_id"),
                        rs.getString("username"),
                        rs.getString("avatar_url"),
                        rs.getLong("total_points"),
                        rs.getLong("ranking_position")
                )
        );
    }

    public List<DailyPointsResponse> findDailyPoints() {
        return jdbc.query(
                "SELECT date, user_id, challenge_id, points" +
                " FROM vw_daily_points ORDER BY date ASC",
                (rs, rowNum) -> new DailyPointsResponse(
                        rs.getObject("date", LocalDate.class),
                        rs.getLong("user_id"),
                        rs.getLong("challenge_id"),
                        rs.getLong("points")
                )
        );
    }

    public List<ChallengeMetricsResponse> findChallengeMetrics() {
        return jdbc.query(
                "SELECT challenge_id, title, difficulty, subject, status," +
                " participant_count, completed_count, in_progress_count," +
                " total_proofs, approved_proofs, avg_progress" +
                " FROM vw_challenge_metrics ORDER BY participant_count DESC",
                (rs, rowNum) -> new ChallengeMetricsResponse(
                        rs.getLong("challenge_id"),
                        rs.getString("title"),
                        rs.getString("difficulty"),
                        rs.getString("subject"),
                        rs.getString("status"),
                        rs.getLong("participant_count"),
                        rs.getLong("completed_count"),
                        rs.getLong("in_progress_count"),
                        rs.getLong("total_proofs"),
                        rs.getLong("approved_proofs"),
                        rs.getDouble("avg_progress")
                )
        );
    }

    public EvidenceMetricsResponse findEvidenceMetrics() {
        return jdbc.queryForObject(
                "SELECT approved_count, rejected_count, pending_count, total_count, approval_rate" +
                " FROM vw_evidence_metrics",
                (rs, rowNum) -> new EvidenceMetricsResponse(
                        rs.getLong("approved_count"),
                        rs.getLong("rejected_count"),
                        rs.getLong("pending_count"),
                        rs.getLong("total_count"),
                        rs.getDouble("approval_rate")
                )
        );
    }

    public EngagementMetricsResponse findEngagementMetrics() {
        String sql = """
                SELECT
                  (SELECT COUNT(*) FROM users)                                                             AS total_users,
                  (SELECT COUNT(DISTINCT user_id) FROM vw_user_ranking WHERE total_points > 0)            AS active_users,
                  ROUND(100.0 * (SELECT COUNT(DISTINCT user_id) FROM vw_user_ranking WHERE total_points > 0)
                        / NULLIF((SELECT COUNT(*) FROM users), 0), 2)                                     AS retention_rate,
                  (SELECT COALESCE(ROUND(AVG(total_points), 2), 0)
                   FROM vw_user_ranking WHERE total_points > 0)                                           AS avg_proofs_per_active_user,
                  (SELECT COALESCE(SUM(total_points), 0) FROM vw_user_ranking)                            AS total_points_distributed,
                  (SELECT subject FROM vw_challenge_metrics
                   WHERE subject IS NOT NULL AND subject <> ''
                   GROUP BY subject ORDER BY SUM(participant_count) DESC LIMIT 1)                         AS top_subject,
                  (SELECT COUNT(*) FROM vw_challenge_metrics WHERE status = 'ACTIVE')                     AS active_challenges,
                  (SELECT COUNT(*) FROM vw_challenge_metrics)                                             AS total_challenges
                """;
        try {
            return jdbc.queryForObject(sql, (rs, n) -> new EngagementMetricsResponse(
                    rs.getLong("total_users"),
                    rs.getLong("active_users"),
                    rs.getDouble("retention_rate"),
                    rs.getDouble("avg_proofs_per_active_user"),
                    rs.getLong("total_points_distributed"),
                    rs.getString("top_subject"),
                    rs.getLong("active_challenges"),
                    rs.getLong("total_challenges")
            ));
        } catch (DataAccessException e) {
            log.warn("Engagement metrics error: {}", e.getMessage());
            return new EngagementMetricsResponse(0, 0, 0, 0, 0, null, 0, 0);
        }
    }
}
