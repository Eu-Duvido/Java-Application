package com.euduvido.euduvido_api.entrypoint.dtos.response;

import java.time.LocalDate;

public record DailyPointsResponse(
        LocalDate date,
        Long userId,
        Long challengeId,
        Long points
) {}
