package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.services.InsightService;
import com.euduvido.euduvido_api.infrastructure.persistence.entities.AiInsightEntity;
import com.euduvido.euduvido_api.infrastructure.persistence.repositories.AiInsightJpaRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para geração e consulta de AI Insights.
 *
 * Endpoints:
 *   POST /api/v1/insights/gerar   — gera novos insights via Gemini e salva no banco
 *   GET  /api/v1/insights         — retorna insights já salvos (sem chamar Gemini)
 *   GET  /api/v1/insights/existe  — verifica se já há insights para o perfil
 */
@RestController
@RequestMapping("/api/v1/insights")
@CrossOrigin(origins = "*")
@Tag(name = "Insights", description = "Insights gerados via IA com base no perfil do usuário (curso, região, modalidade)")
public class InsightController {

    @Autowired
    private InsightService insightService;

    @Autowired
    private AiInsightJpaRepository insightRepository;

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Gera os 3 insights para o perfil do usuário chamando o Gemini.
     * Chamar no login quando não existirem insights salvos.
     *
     * POST /api/v1/insights/gerar?curso=Direito&regiao=Nordeste&modalidade=1
     */
    @PostMapping("/gerar")
    public ResponseEntity<?> gerar(
            @RequestParam String curso,
            @RequestParam String regiao,
            @RequestParam(defaultValue = "1") int modalidade,
            @RequestParam(defaultValue = "") String areaGeral) {
        try {
            List<AiInsightEntity> insights = insightService.gerarInsights(
                    curso, regiao, modalidade, areaGeral);
            return ResponseEntity.ok(insights);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao gerar insights: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Retorna os insights já salvos. Use nas visitas subsequentes do usuário.
     *
     * GET /api/v1/insights?curso=Direito&regiao=Nordeste
     */
    @GetMapping
    public List<AiInsightEntity> listar(
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) String regiao) {
        if (curso != null && !curso.isBlank() && regiao != null && !regiao.isBlank()) {
            return insightRepository.findByNoCursoAndNoRegiaoOrderByDtGeracaoDesc(curso, regiao);
        }
        return insightRepository.findTop10ByOrderByDtGeracaoDesc();
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verifica se já existem insights salvos para o perfil.
     * O front usa isso para decidir entre /gerar ou /listar.
     *
     * GET /api/v1/insights/existe?curso=Direito&regiao=Nordeste&modalidade=1
     * Retorna: true | false
     */
    @GetMapping("/existe")
    public boolean existe(
            @RequestParam String curso,
            @RequestParam String regiao,
            @RequestParam(defaultValue = "1") int modalidade) {
        return insightRepository
                .existsByNoCursoAndNoRegiaoAndTpModalidade(curso, regiao, modalidade);
    }
}
