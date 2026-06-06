package com.euduvido.euduvido_api.infrastructure.inep;

import com.euduvido.euduvido_api.entrypoint.dtos.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InepDashboardRepository {

    private static final Logger log = LoggerFactory.getLogger(InepDashboardRepository.class);

    private final JdbcTemplate jdbc;

    public InepDashboardRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private <T> List<T> safe(String sql, org.springframework.jdbc.core.RowMapper<T> mapper) {
        try {
            return jdbc.query(sql, mapper);
        } catch (DataAccessException e) {
            log.warn("INEP view indisponível — execute o data-pipeline primeiro. Detalhe: {}", e.getMessage());
            return List.of();
        }
    }

    private long longVal(java.sql.ResultSet rs, String col) throws java.sql.SQLException {
        long v = rs.getLong(col);
        return rs.wasNull() ? 0L : v;
    }

    private double doubleVal(java.sql.ResultSet rs, String col) throws java.sql.SQLException {
        double v = rs.getDouble(col);
        return rs.wasNull() ? 0.0 : v;
    }

    // ── 1. Resumo Geral ───────────────────────────────────────────────────────

    public List<ResumoGeralResponse> findResumoGeral() {
        String sql = """
                SELECT nu_ano_censo, total_ies, total_cursos, total_vagas,
                       total_ingressantes, total_matriculados, total_concluintes,
                       pct_ing_feminino, pct_ing_masculino, taxa_conclusao_pct,
                       total_ing_via_enem, pct_enem
                FROM vw_resumo_geral
                ORDER BY nu_ano_censo DESC
                """;
        return safe(sql, (rs, n) -> new ResumoGeralResponse(
                rs.getInt("nu_ano_censo"),
                longVal(rs, "total_ies"),
                longVal(rs, "total_cursos"),
                longVal(rs, "total_vagas"),
                longVal(rs, "total_ingressantes"),
                longVal(rs, "total_matriculados"),
                longVal(rs, "total_concluintes"),
                doubleVal(rs, "pct_ing_feminino"),
                doubleVal(rs, "pct_ing_masculino"),
                doubleVal(rs, "taxa_conclusao_pct"),
                longVal(rs, "total_ing_via_enem"),
                doubleVal(rs, "pct_enem")
        ));
    }

    // ── 2. Ranking Cursos por Região (top 5 por região) ───────────────────────

    public List<RankingCursosRegiaoResponse> findRankingCursosRegiao() {
        String sql = """
                SELECT no_regiao, no_curso, no_area_geral,
                       total_ingressantes, pct_conclusao, rank_na_regiao
                FROM vw_ranking_cursos_regiao
                WHERE rank_na_regiao <= 5
                ORDER BY no_regiao, rank_na_regiao
                """;
        return safe(sql, (rs, n) -> new RankingCursosRegiaoResponse(
                rs.getString("no_regiao"),
                rs.getString("no_curso"),
                rs.getString("no_area_geral"),
                longVal(rs, "total_ingressantes"),
                doubleVal(rs, "pct_conclusao"),
                longVal(rs, "rank_na_regiao")
        ));
    }

    // ── 3. Ranking Cursos por Área (top 10 áreas gerais) ─────────────────────

    public List<RankingCursosAreaResponse> findRankingCursosArea() {
        String sql = """
                SELECT no_area_geral,
                       SUM(total_ingressantes)  AS total_ingressantes,
                       SUM(total_matriculados)  AS total_matriculados,
                       SUM(total_concluintes)   AS total_concluintes,
                       COUNT(DISTINCT no_curso) AS num_cursos,
                       ROUND(100.0 * SUM(total_concluintes) / NULLIF(SUM(total_matriculados), 0), 2) AS pct_conclusao
                FROM vw_ranking_cursos_area
                GROUP BY no_area_geral
                ORDER BY total_ingressantes DESC
                LIMIT 10
                """;
        return safe(sql, (rs, n) -> new RankingCursosAreaResponse(
                rs.getString("no_area_geral"),
                longVal(rs, "total_ingressantes"),
                longVal(rs, "total_matriculados"),
                longVal(rs, "total_concluintes"),
                longVal(rs, "num_cursos"),
                doubleVal(rs, "pct_conclusao")
        ));
    }

    // ── 4. Distribuição de Gênero (agrupado por ano) ──────────────────────────

    public List<GeneroIngressantesResponse> findGeneroIngressantes() {
        String sql = """
                SELECT nu_ano_censo,
                       SUM(total_ingressantes) AS total_ingressantes,
                       SUM(feminino)           AS feminino,
                       SUM(masculino)          AS masculino,
                       ROUND(100.0 * SUM(feminino)  / NULLIF(SUM(total_ingressantes), 0), 2) AS pct_feminino,
                       ROUND(100.0 * SUM(masculino) / NULLIF(SUM(total_ingressantes), 0), 2) AS pct_masculino
                FROM vw_demografico_genero_ingressantes
                GROUP BY nu_ano_censo
                ORDER BY nu_ano_censo DESC
                LIMIT 5
                """;
        return safe(sql, (rs, n) -> new GeneroIngressantesResponse(
                rs.getInt("nu_ano_censo"),
                longVal(rs, "total_ingressantes"),
                longVal(rs, "feminino"),
                longVal(rs, "masculino"),
                doubleVal(rs, "pct_feminino"),
                doubleVal(rs, "pct_masculino")
        ));
    }

    // ── 5. Distribuição Racial (agregado nacional) ────────────────────────────

    public List<RacaIngressantesResponse> findRacaIngressantes() {
        String sql = """
                SELECT SUM(total_ingressantes) AS total_ingressantes,
                       SUM(branca)             AS branca,
                       SUM(preta)              AS preta,
                       SUM(parda)              AS parda,
                       SUM(amarela)            AS amarela,
                       SUM(indigena)           AS indigena,
                       SUM(cor_nao_declarada)  AS cor_nao_declarada,
                       ROUND(100.0 * SUM(branca)  / NULLIF(SUM(total_ingressantes), 0), 2) AS pct_branca,
                       ROUND(100.0 * SUM(preta)   / NULLIF(SUM(total_ingressantes), 0), 2) AS pct_preta,
                       ROUND(100.0 * SUM(parda)   / NULLIF(SUM(total_ingressantes), 0), 2) AS pct_parda,
                       ROUND(100.0 * (SUM(preta) + SUM(parda)) / NULLIF(SUM(total_ingressantes), 0), 2) AS pct_preto_pardo
                FROM vw_demografico_raca_ingressantes
                """;
        return safe(sql, (rs, n) -> new RacaIngressantesResponse(
                longVal(rs, "total_ingressantes"),
                longVal(rs, "branca"),
                longVal(rs, "preta"),
                longVal(rs, "parda"),
                longVal(rs, "amarela"),
                longVal(rs, "indigena"),
                longVal(rs, "cor_nao_declarada"),
                doubleVal(rs, "pct_branca"),
                doubleVal(rs, "pct_preta"),
                doubleVal(rs, "pct_parda"),
                doubleVal(rs, "pct_preto_pardo")
        ));
    }

    // ── 6. Distribuição Etária (agregado nacional) ────────────────────────────

    public List<EtariaIngressantesResponse> findEtariaIngressantes() {
        String sql = """
                SELECT SUM(total_ingressantes) AS total_ingressantes,
                       SUM(faixa_0_17)         AS faixa017,
                       SUM(faixa_18_24)        AS faixa1824,
                       SUM(faixa_25_29)        AS faixa2529,
                       SUM(faixa_30_34)        AS faixa3034,
                       SUM(faixa_35_39)        AS faixa3539,
                       SUM(faixa_40_49)        AS faixa4049,
                       SUM(faixa_50_59)        AS faixa5059,
                       SUM(faixa_60_mais)      AS faixa60mais,
                       ROUND(100.0 * SUM(faixa_18_24) / NULLIF(SUM(total_ingressantes), 0), 2) AS pct1824
                FROM vw_demografico_etaria_ingressantes
                """;
        return safe(sql, (rs, n) -> new EtariaIngressantesResponse(
                longVal(rs, "total_ingressantes"),
                longVal(rs, "faixa017"),
                longVal(rs, "faixa1824"),
                longVal(rs, "faixa2529"),
                longVal(rs, "faixa3034"),
                longVal(rs, "faixa3539"),
                longVal(rs, "faixa4049"),
                longVal(rs, "faixa5059"),
                longVal(rs, "faixa60mais"),
                doubleVal(rs, "pct1824")
        ));
    }

    // ── 7. EAD vs Presencial (agrupado por modalidade) ───────────────────────

    public List<EadVsPresencialResponse> findEadVsPresencial() {
        String sql = """
                SELECT modalidade,
                       SUM(total_ingressantes) AS total_ingressantes,
                       SUM(total_matriculados) AS total_matriculados,
                       SUM(total_concluintes)  AS total_concluintes,
                       SUM(total_vagas)        AS total_vagas,
                       SUM(num_cursos)         AS num_cursos
                FROM vw_ead_vs_presencial
                GROUP BY modalidade
                ORDER BY total_ingressantes DESC
                """;
        return safe(sql, (rs, n) -> new EadVsPresencialResponse(
                rs.getString("modalidade"),
                longVal(rs, "total_ingressantes"),
                longVal(rs, "total_matriculados"),
                longVal(rs, "total_concluintes"),
                longVal(rs, "total_vagas"),
                longVal(rs, "num_cursos")
        ));
    }

    // ── 8. Taxa de Conclusão por IES (top 20) ────────────────────────────────

    public List<TaxaConclusaoIesResponse> findTaxaConclusaoIes() {
        String sql = """
                SELECT no_ies, sg_ies, no_regiao, sg_uf,
                       SUM(total_matriculados) AS total_matriculados,
                       SUM(total_concluintes)  AS total_concluintes,
                       ROUND(100.0 * SUM(total_concluintes) / NULLIF(SUM(total_matriculados), 0), 2) AS taxa_conclusao_pct,
                       ROUND(100.0 * (SUM(trancamentos) + SUM(desvinculados)) / NULLIF(SUM(total_matriculados), 0), 2) AS taxa_evasao_pct
                FROM vw_taxa_conclusao_ies
                WHERE total_matriculados > 100
                GROUP BY no_ies, sg_ies, no_regiao, sg_uf
                HAVING SUM(total_matriculados) > 500
                ORDER BY taxa_conclusao_pct DESC
                LIMIT 20
                """;
        return safe(sql, (rs, n) -> new TaxaConclusaoIesResponse(
                rs.getString("no_ies"),
                rs.getString("sg_ies"),
                rs.getString("no_regiao"),
                rs.getString("sg_uf"),
                longVal(rs, "total_matriculados"),
                longVal(rs, "total_concluintes"),
                doubleVal(rs, "taxa_conclusao_pct"),
                doubleVal(rs, "taxa_evasao_pct")
        ));
    }
}
