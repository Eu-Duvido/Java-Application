package com.euduvido.euduvido_api.entrypoint.dtos.response;

public record EngagementMetricsResponse(
        long totalUsers,
        long activeUsers,
        double retentionRate,
        double avgProofsPerActiveUser,
        long totalPointsDistributed,
        String topSubject,
        long activeChallenges,
        long totalChallenges
) {}
