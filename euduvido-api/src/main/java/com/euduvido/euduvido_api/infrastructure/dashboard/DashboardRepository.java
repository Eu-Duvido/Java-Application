package com.euduvido.euduvido_api.infrastructure.dashboard;

import com.euduvido.euduvido_api.entrypoint.dtos.response.ChallengeMetricsResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.DailyPointsResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.EvidenceMetricsResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.UserRankingResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class DashboardRepository {

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
}
