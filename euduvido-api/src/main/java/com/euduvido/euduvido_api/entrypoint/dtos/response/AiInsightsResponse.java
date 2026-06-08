package com.euduvido.euduvido_api.entrypoint.dtos.response;

import java.time.LocalDateTime;
import java.util.List;

public record AiInsightsResponse(
        List<AiInsight> insights,
        LocalDateTime generatedAt
) {}
