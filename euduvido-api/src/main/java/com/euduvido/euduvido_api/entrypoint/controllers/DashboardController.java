package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.services.DashboardService;
import com.euduvido.euduvido_api.entrypoint.dtos.response.ChallengeMetricsResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.DailyPointsResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.EngagementMetricsResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.EvidenceMetricsResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.UserRankingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Métricas da plataforma — leitura direta das views vw_*")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @Operation(summary = "Ranking geral de usuários por pontos (vw_user_ranking)")
    @ApiResponse(responseCode = "200", description = "Lista retornada em ordem crescente de posição")
    @GetMapping("/ranking")
    public ResponseEntity<List<UserRankingResponse>> getRanking() {
        return ResponseEntity.ok(service.getRanking());
    }

    @Operation(summary = "Pontos diários por usuário e desafio (vw_daily_points)")
    @ApiResponse(responseCode = "200", description = "Lista em ordem cronológica")
    @GetMapping("/daily-points")
    public ResponseEntity<List<DailyPointsResponse>> getDailyPoints() {
        return ResponseEntity.ok(service.getDailyPoints());
    }

    @Operation(summary = "Métricas por desafio (vw_challenge_metrics)")
    @ApiResponse(responseCode = "200", description = "Lista ordenada por número de participantes")
    @GetMapping("/challenge-metrics")
    public ResponseEntity<List<ChallengeMetricsResponse>> getChallengeMetrics() {
        return ResponseEntity.ok(service.getChallengeMetrics());
    }

    @Operation(summary = "Métricas globais de evidências (vw_evidence_metrics)")
    @ApiResponse(responseCode = "200", description = "Objeto único com contagens e taxa de aprovação")
    @GetMapping("/evidence-metrics")
    public ResponseEntity<EvidenceMetricsResponse> getEvidenceMetrics() {
        return ResponseEntity.ok(service.getEvidenceMetrics());
    }

    @Operation(summary = "Métricas de engajamento — retenção, usuários ativos, consistência")
    @ApiResponse(responseCode = "200", description = "KPIs de engajamento calculados sobre dados reais")
    @GetMapping("/engagement")
    public ResponseEntity<EngagementMetricsResponse> getEngagementMetrics() {
        return ResponseEntity.ok(service.getEngagementMetrics());
    }
}
