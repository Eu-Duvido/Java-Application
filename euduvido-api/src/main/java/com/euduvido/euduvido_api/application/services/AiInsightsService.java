package com.euduvido.euduvido_api.application.services;

import com.euduvido.euduvido_api.entrypoint.dtos.response.AiInsight;

import java.util.List;

public interface AiInsightsService {
    List<AiInsight> generateDashboardInsights(DashboardInsightsInput input);
}
