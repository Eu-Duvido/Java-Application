package com.euduvido.euduvido_api.application.usecases.dashboard;

import com.euduvido.euduvido_api.application.services.AiInsightsService;
import com.euduvido.euduvido_api.application.services.DashboardInsightsInput;
import com.euduvido.euduvido_api.application.services.DashboardService;
import com.euduvido.euduvido_api.application.services.InepDashboardService;
import com.euduvido.euduvido_api.entrypoint.dtos.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class GenerateEducationalInsightsUseCase {

    private static final Logger log = LoggerFactory.getLogger(GenerateEducationalInsightsUseCase.class);

    private final DashboardService dashboardService;
    private final InepDashboardService inepDashboardService;
    private final AiInsightsService aiInsightsService;

    public GenerateEducationalInsightsUseCase(
            DashboardService dashboardService,
            InepDashboardService inepDashboardService,
            AiInsightsService aiInsightsService) {
        this.dashboardService     = dashboardService;
        this.inepDashboardService = inepDashboardService;
        this.aiInsightsService    = aiInsightsService;
    }

    public AiInsightsResponse execute() {
        try {
            EngagementMetricsResponse   engagement = dashboardService.getEngagementMetrics();
            EvidenceMetricsResponse     evidence   = dashboardService.getEvidenceMetrics();
            List<ChallengeMetricsResponse> challenges = dashboardService.getChallengeMetrics();

            List<ResumoGeralResponse>        resumo = inepDashboardService.getResumoGeral();
            List<GeneroIngressantesResponse> genero = inepDashboardService.getGeneroIngressantes();
            List<EtariaIngressantesResponse> etaria = inepDashboardService.getEtariaIngressantes();
            List<EadVsPresencialResponse>    ead    = inepDashboardService.getEadVsPresencial();

            DashboardInsightsInput input = buildInput(engagement, evidence, challenges, resumo, genero, etaria, ead);
            List<AiInsight> insights = aiInsightsService.generateDashboardInsights(input);
            return new AiInsightsResponse(insights, LocalDateTime.now());
        } catch (Exception e) {
            log.error("Falha ao executar GenerateEducationalInsightsUseCase: {}", e.getMessage());
            return new AiInsightsResponse(List.of(), LocalDateTime.now());
        }
    }

    // ─── Data aggregation ────────────────────────────────────────────────────────

    private DashboardInsightsInput buildInput(
            EngagementMetricsResponse engagement,
            EvidenceMetricsResponse evidence,
            List<ChallengeMetricsResponse> challenges,
            List<ResumoGeralResponse> resumo,
            List<GeneroIngressantesResponse> genero,
            List<EtariaIngressantesResponse> etaria,
            List<EadVsPresencialResponse> ead) {

        // App metrics
        long totalUsers      = engagement != null ? engagement.totalUsers()      : 0L;
        long activeUsers     = engagement != null ? engagement.activeUsers()     : 0L;
        double retentionRate = engagement != null ? engagement.retentionRate()   : 0.0;
        String topSubject    = engagement != null ? engagement.topSubject()      : null;
        long activeChallenges = engagement != null ? engagement.activeChallenges() : 0L;

        double approvalRate  = evidence != null && evidence.approvalRate()  != null ? evidence.approvalRate()  : 0.0;
        long approvedProofs  = evidence != null && evidence.approvedCount() != null ? evidence.approvedCount() : 0L;
        long totalProofs     = evidence != null && evidence.totalCount()    != null ? evidence.totalCount()    : 0L;

        List<ChallengeMetricsResponse> withParticipants = (challenges == null ? List.<ChallengeMetricsResponse>of() : challenges)
                .stream()
                .filter(c -> c.participantCount() != null && c.participantCount() > 0)
                .toList();

        List<DashboardInsightsInput.ChallengeSummary> topChallenges = withParticipants.stream()
                .sorted((a, b) -> Long.compare(
                        b.participantCount() != null ? b.participantCount() : 0L,
                        a.participantCount() != null ? a.participantCount() : 0L))
                .limit(3)
                .map(c -> {
                    long part = c.participantCount() != null ? c.participantCount() : 0L;
                    long done = c.completedCount()   != null ? c.completedCount()   : 0L;
                    int  cr   = part > 0 ? (int) Math.round((done * 100.0) / part) : 0;
                    return new DashboardInsightsInput.ChallengeSummary(c.title(), part, done, cr);
                })
                .toList();

        // INEP metrics
        ResumoGeralResponse r0 = (resumo == null || resumo.isEmpty()) ? null : resumo.get(0);
        long totalIngressantes  = r0 != null && r0.totalIngressantes() != null ? r0.totalIngressantes() : 0L;
        double pctIngFeminino   = r0 != null && r0.pctIngFeminino()    != null ? r0.pctIngFeminino()    : 0.0;
        double pctIngMasculino  = r0 != null && r0.pctIngMasculino()   != null ? r0.pctIngMasculino()   : 0.0;
        double taxaConclusaoPct = r0 != null && r0.taxaConclusaoPct()  != null ? r0.taxaConclusaoPct()  : 0.0;
        double pctEnem          = r0 != null && r0.pctEnem()           != null ? r0.pctEnem()           : 0.0;
        int anoCenso            = r0 != null && r0.nuAnoCenso()        != null ? r0.nuAnoCenso()        : 0;

        EtariaIngressantesResponse et0 = (etaria == null || etaria.isEmpty()) ? null : etaria.get(0);
        double pct1824 = et0 != null && et0.pct1824() != null ? et0.pct1824() : 0.0;

        // EAD percentage
        long totalEadIng = (ead == null ? List.<EadVsPresencialResponse>of() : ead).stream()
                .mapToLong(e -> e.totalIngressantes() != null ? e.totalIngressantes() : 0L)
                .sum();
        long eadIng = (ead == null ? List.<EadVsPresencialResponse>of() : ead).stream()
                .filter(e -> "EAD".equalsIgnoreCase(e.modalidade()))
                .mapToLong(e -> e.totalIngressantes() != null ? e.totalIngressantes() : 0L)
                .sum();
        double pctEad = totalEadIng > 0 ? Math.round((eadIng * 100.0) / totalEadIng) : 0.0;

        return new DashboardInsightsInput(
                totalUsers, activeUsers, retentionRate, approvalRate,
                approvedProofs, totalProofs, activeChallenges, topSubject,
                withParticipants.size(), topChallenges,
                totalIngressantes, pctIngFeminino, pctIngMasculino,
                taxaConclusaoPct, pctEnem, pct1824, pctEad, anoCenso
        );
    }
}
