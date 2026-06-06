package com.euduvido.euduvido_api.entrypoint.dtos.response;

public record ChallengeMetricsResponse(
        Long challengeId,
        String title,
        String difficulty,
        String subject,
        String status,
        Long participantCount,
        Long completedCount,
        Long inProgressCount,
        Long totalProofs,
        Long approvedProofs,
        Double avgProgress
) {}
