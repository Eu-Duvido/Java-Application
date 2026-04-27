package com.euduvido.euduvido_api.application.services;

import com.euduvido.euduvido_api.entrypoint.dtos.response.ChallengeMetricsResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.DailyPointsResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.EngagementMetricsResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.EvidenceMetricsResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.UserRankingResponse;
import com.euduvido.euduvido_api.infrastructure.dashboard.DashboardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final DashboardRepository repository;

    public DashboardService(DashboardRepository repository) {
        this.repository = repository;
    }

    public List<UserRankingResponse> getRanking() {
        return repository.findUserRanking();
    }

    public List<DailyPointsResponse> getDailyPoints() {
        return repository.findDailyPoints();
    }

    public List<ChallengeMetricsResponse> getChallengeMetrics() {
        return repository.findChallengeMetrics();
    }

    public EvidenceMetricsResponse getEvidenceMetrics() {
        return repository.findEvidenceMetrics();
    }

    public EngagementMetricsResponse getEngagementMetrics() {
        return repository.findEngagementMetrics();
    }
}
