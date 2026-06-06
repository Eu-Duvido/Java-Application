package com.euduvido.euduvido_api.entrypoint.dtos.response;

public record UserRankingResponse(
        Long userId,
        String username,
        String avatarUrl,
        Long totalPoints,
        Long rankingPosition
) {}
