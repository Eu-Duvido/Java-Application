package com.euduvido.euduvido_api.application.services;

import java.util.List;

public record DashboardInsightsInput(
        // App metrics
        long totalUsers,
        long activeUsers,
        double retentionRate,
        double approvalRate,
        long approvedProofs,
        long totalProofs,
        long activeChallenges,
        String topSubject,
        int totalChallengesWithParticipants,
        List<ChallengeSummary> topChallenges,
        // INEP metrics
        long totalIngressantes,
        double pctIngFeminino,
        double pctIngMasculino,
        double taxaConclusaoPct,
        double pctEnem,
        double pct1824,
        double pctEad,
        int anoCenso
) {
    public record ChallengeSummary(
            String title,
            long participants,
            long completed,
            int completionRate
    ) {}
}
