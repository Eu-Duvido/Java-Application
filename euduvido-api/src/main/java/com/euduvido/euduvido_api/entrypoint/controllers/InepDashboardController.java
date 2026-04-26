package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.services.InepDashboardService;
import com.euduvido.euduvido_api.entrypoint.dtos.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inep")
@Tag(name = "INEP Dashboard", description = "Dados educacionais do censo INEP — views vw_*")
public class InepDashboardController {

    private final InepDashboardService service;

    public InepDashboardController(InepDashboardService service) {
        this.service = service;
    }

    @Operation(summary = "KPIs gerais do censo (vw_resumo_geral)")
    @GetMapping("/resumo-geral")
    public ResponseEntity<List<ResumoGeralResponse>> getResumoGeral() {
        return ResponseEntity.ok(service.getResumoGeral());
    }

    @Operation(summary = "Top 5 cursos por região (vw_ranking_cursos_regiao)")
    @GetMapping("/ranking-cursos-regiao")
    public ResponseEntity<List<RankingCursosRegiaoResponse>> getRankingCursosRegiao() {
        return ResponseEntity.ok(service.getRankingCursosRegiao());
    }

    @Operation(summary = "Top 10 áreas por ingressantes (vw_ranking_cursos_area)")
    @GetMapping("/ranking-cursos-area")
    public ResponseEntity<List<RankingCursosAreaResponse>> getRankingCursosArea() {
        return ResponseEntity.ok(service.getRankingCursosArea());
    }

    @Operation(summary = "Distribuição de gênero por ano (vw_demografico_genero_ingressantes)")
    @GetMapping("/genero-ingressantes")
    public ResponseEntity<List<GeneroIngressantesResponse>> getGeneroIngressantes() {
        return ResponseEntity.ok(service.getGeneroIngressantes());
    }

    @Operation(summary = "Distribuição racial nacional (vw_demografico_raca_ingressantes)")
    @GetMapping("/raca-ingressantes")
    public ResponseEntity<List<RacaIngressantesResponse>> getRacaIngressantes() {
        return ResponseEntity.ok(service.getRacaIngressantes());
    }

    @Operation(summary = "Distribuição etária nacional (vw_demografico_etaria_ingressantes)")
    @GetMapping("/etaria-ingressantes")
    public ResponseEntity<List<EtariaIngressantesResponse>> getEtariaIngressantes() {
        return ResponseEntity.ok(service.getEtariaIngressantes());
    }

    @Operation(summary = "EAD vs Presencial por modalidade (vw_ead_vs_presencial)")
    @GetMapping("/ead-vs-presencial")
    public ResponseEntity<List<EadVsPresencialResponse>> getEadVsPresencial() {
        return ResponseEntity.ok(service.getEadVsPresencial());
    }

    @Operation(summary = "Top 20 IES por taxa de conclusão (vw_taxa_conclusao_ies)")
    @GetMapping("/taxa-conclusao-ies")
    public ResponseEntity<List<TaxaConclusaoIesResponse>> getTaxaConclusaoIes() {
        return ResponseEntity.ok(service.getTaxaConclusaoIes());
    }
}
