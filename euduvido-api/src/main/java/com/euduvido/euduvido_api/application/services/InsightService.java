package com.euduvido.euduvido_api.application.services;

import com.euduvido.euduvido_api.infrastructure.persistence.entities.AiInsightEntity;
import com.euduvido.euduvido_api.infrastructure.persistence.repositories.AiInsightJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço responsável por:
 *   1. Consultar as views INEP diretamente no MySQL (mesmo banco do Data-Pipeline)
 *   2. Enviar os dados ao Gemini para geração dos insights
 *   3. Parsear a resposta do Gemini
 *   4. Salvar os insights na tabela ai_insights
 */
@Service
public class InsightService {

    private static final Logger log = LoggerFactory.getLogger(InsightService.class);

    @Value("${app.gemini.api-key}")
    private String geminiApiKey;

    @Value("${app.gemini.base-url:https://generativelanguage.googleapis.com}")
    private String geminiBaseUrl;

    @Value("${app.gemini.model:gemini-flash-latest}")
    private String geminiModel;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private AiInsightJpaRepository insightRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ─────────────────────────────────────────────────────────────────────────
    // Método principal
    // ─────────────────────────────────────────────────────────────────────────

    public List<AiInsightEntity> gerarInsights(
            String curso,
            String regiao,
            int modalidade,
            String areaGeral) throws Exception {

        // 1. Busca dados das views MySQL diretamente (sem depender do Python)
        Map<String, Object> dadosDW = buscarDadosDW(curso, regiao, modalidade, areaGeral);

        List<Map<String, Object>> insightsJson;
        String fonte = "gemini";

        try {
            // 2. Monta o prompt completo e envia ao Gemini
            String promptSugerido = (String) dadosDW.get("prompt_sugerido");
            String promptCompleto = promptSugerido
                    + "\n\nDados do Censo INEP 2024:\n"
                    + objectMapper.writeValueAsString(dadosDW);

            String respostaGemini = chamarGemini(promptCompleto);

            // 3. Parseia o JSON retornado pelo Gemini
            insightsJson = objectMapper.readValue(
                    limparJsonGemini(respostaGemini),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
            );
        } catch (Exception e) {
            log.warn("Falha ao gerar insights com Gemini. Usando fallback local.", e);
            insightsJson = gerarInsightsFallback(dadosDW, curso, regiao, modalidade, areaGeral);
            fonte = "fallback";
        }

        // 4. Salva cada insight no banco
        List<AiInsightEntity> salvos = new ArrayList<>();
        for (Map<String, Object> ins : insightsJson) {
            AiInsightEntity entity = mapearInsight(ins, curso, regiao, modalidade, areaGeral, fonte);
            salvos.add(insightRepository.save(entity));
        }

        return salvos;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Consulta direta às views MySQL (replica lógica do Python /insights/dados-perfil)
    // ─────────────────────────────────────────────────────────────────────────

    private Map<String, Object> buscarDadosDW(
            String curso, String regiao, int modalidade, String areaGeral) {

        List<Map<String, Object>> dadosCursoNacional = jdbc.queryForList(
            """
            SELECT no_curso, no_area_geral, no_area_especifica,
                   total_ingressantes, total_vagas, total_matriculados,
                   total_concluintes, num_ies_ofertantes, pct_conclusao
            FROM   vw_ranking_cursos_area
            WHERE  no_curso = ?
            LIMIT  1
            """,
            curso
        );

        List<Map<String, Object>> dadosCursoRegiao = jdbc.queryForList(
            """
            SELECT no_regiao, no_curso, no_area_geral,
                   total_ingressantes, total_matriculados, total_concluintes,
                   total_vagas, pct_conclusao, pct_ocupacao_vagas, rank_na_regiao
            FROM   vw_ranking_cursos_regiao
            WHERE  no_curso  = ?
              AND  no_regiao = ?
            LIMIT  1
            """,
            curso, regiao
        );

        List<Map<String, Object>> eadVsPresencial = jdbc.queryForList(
            """
            SELECT modalidade, total_matriculados,
                   total_ingressantes, total_concluintes, num_cursos
            FROM   vw_ead_vs_presencial
            WHERE  no_regiao = ?
            ORDER  BY total_matriculados DESC
            """,
            regiao
        );

        List<Map<String, Object>> kpisNacionais = jdbc.queryForList(
            "SELECT * FROM vw_resumo_geral ORDER BY nu_ano_censo DESC LIMIT 1"
        );

        String modalidadeLabel = modalidade == 1 ? "Presencial" : "EAD";

        String promptSugerido = String.format(
            "Você é um coach de estudos. Com base nos dados reais do Censo da " +
            "Educação Superior Brasileira (INEP 2024) abaixo, gere exatamente 3 insights " +
            "personalizados e motivadores para um estudante do curso de %s " +
            "na região %s na modalidade %s. " +
            "Cite os números dos dados. Responda em JSON com a estrutura: " +
            "[{\"tipo\": \"competitividade|conclusao|modalidade\", " +
            "\"titulo\": \"...\", \"descricao\": \"...\", " +
            "\"valor_destaque\": 0.0, \"unidade\": \"...\", " +
            "\"interpretacao\": \"...\", \"nivel\": \"alto|medio|baixo\"}]",
            curso, regiao, modalidadeLabel
        );

        Map<String, Object> result = new HashMap<>();
        result.put("perfil_usuario", Map.of(
                "no_curso",      curso,
                "no_area_geral", areaGeral != null ? areaGeral : "",
                "no_regiao",     regiao,
                "tp_modalidade", modalidade,
                "modalidade",    modalidadeLabel
        ));
        result.put("dados_curso_nacional",  dadosCursoNacional);
        result.put("dados_curso_na_regiao", dadosCursoRegiao);
        result.put("ead_vs_presencial",     eadVsPresencial);
        result.put("kpis_censo_nacional",   kpisNacionais);
        result.put("prompt_sugerido",       promptSugerido);

        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Chamada ao Gemini
    // ─────────────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private String chamarGemini(String prompt) throws Exception {
        String geminiUrl = geminiBaseUrl + "/v1beta/models/"
                + geminiModel + ":generateContent?key=" + geminiApiKey;

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                ))
        );

        Map<String, Object> resposta = restTemplate.postForObject(geminiUrl, body, Map.class);

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) resposta.get("candidates");
        Map<String, Object> content          = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts      = (List<Map<String, Object>>) content.get("parts");
        return (String) parts.get(0).get("text");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private String limparJsonGemini(String texto) {
        return texto
                .replaceAll("(?s)```json\\s*", "")
                .replaceAll("```", "")
                .trim();
    }

    private AiInsightEntity mapearInsight(
            Map<String, Object> ins,
            String curso, String regiao, int modalidade, String areaGeral, String fonte) {

        AiInsightEntity entity = new AiInsightEntity();
        entity.setNoCurso(curso);
        entity.setNoAreaGeral(areaGeral != null ? areaGeral : "");
        entity.setNoRegiao(regiao);
        entity.setTpModalidade(modalidade);
        entity.setTipo((String) ins.get("tipo"));
        entity.setTitulo(truncar((String) ins.get("titulo"), 255));
        entity.setDescricao((String) ins.get("descricao"));
        entity.setValorDestaque(toDouble(ins.get("valor_destaque")));
        entity.setUnidade(truncar((String) ins.get("unidade"), 100));
        entity.setInterpretacao((String) ins.get("interpretacao"));
        entity.setNivel(validarNivel((String) ins.get("nivel")));
        entity.setFonte(fonte);
        entity.setDtGeracao(LocalDateTime.now());

        Object grafico = ins.get("dados_grafico");
        try {
            entity.setDadosGrafico(grafico != null
                    ? objectMapper.writeValueAsString(grafico)
                    : "{}");
        } catch (Exception e) {
            entity.setDadosGrafico("{}");
        }

        return entity;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> gerarInsightsFallback(
            Map<String, Object> dadosDW,
            String curso,
            String regiao,
            int modalidade,
            String areaGeral) {

        Map<String, Object> cursoNacional = primeiroRegistro(dadosDW.get("dados_curso_nacional"));
        Map<String, Object> cursoRegiao = primeiroRegistro(dadosDW.get("dados_curso_na_regiao"));
        List<Map<String, Object>> modalidades = dadosDW.get("ead_vs_presencial") instanceof List
                ? (List<Map<String, Object>>) dadosDW.get("ead_vs_presencial")
                : List.of();

        double pctOcupacao = toDouble(cursoRegiao.get("pct_ocupacao_vagas"));
        double pctConclusao = toDouble(valorOuPadrao(
                cursoRegiao.get("pct_conclusao"),
                cursoNacional.get("pct_conclusao")
        ));
        double matriculadosModalidade = matriculadosDaModalidade(modalidades, modalidade);
        double totalMatriculadosRegiao = modalidades.stream()
                .mapToDouble(item -> toDouble(item.get("total_matriculados")))
                .sum();
        double participacaoModalidade = totalMatriculadosRegiao > 0
                ? (matriculadosModalidade / totalMatriculadosRegiao) * 100
                : 0.0;

        List<Map<String, Object>> fallback = new ArrayList<>();
        fallback.add(Map.of(
                "tipo", "competitividade",
                "titulo", "Demanda do curso na regiao",
                "descricao", String.format(
                        "Para %s em %s, os dados indicam %.1f%% de ocupacao das vagas.",
                        curso, regiao, pctOcupacao),
                "valor_destaque", pctOcupacao,
                "unidade", "% de ocupacao",
                "interpretacao", pctOcupacao >= 70.0
                        ? "A procura esta aquecida. Vale se preparar com consistencia para se destacar."
                        : "Ainda ha espaco para crescimento. Uma preparacao bem direcionada pode virar vantagem.",
                "nivel", nivelPorPercentual(pctOcupacao)
        ));
        fallback.add(Map.of(
                "tipo", "conclusao",
                "titulo", "Conclusao como sinal de persistencia",
                "descricao", String.format(
                        "O percentual de conclusao disponivel para %s e de %.1f%%.",
                        curso, pctConclusao),
                "valor_destaque", pctConclusao,
                "unidade", "% de conclusao",
                "interpretacao", pctConclusao >= 50.0
                        ? "Os dados mostram um caminho possivel para quem mantem ritmo e planejamento."
                        : "A conclusao exige disciplina. Transformar rotina em habito pode fazer diferenca.",
                "nivel", nivelPorPercentual(pctConclusao)
        ));
        fallback.add(Map.of(
                "tipo", "modalidade",
                "titulo", "Forca da modalidade escolhida",
                "descricao", String.format(
                        "A modalidade %s representa %.1f%% das matriculas mapeadas na regiao.",
                        modalidade == 1 ? "Presencial" : "EAD", participacaoModalidade),
                "valor_destaque", participacaoModalidade,
                "unidade", "% das matriculas",
                "interpretacao", participacaoModalidade >= 50.0
                        ? "Sua modalidade tem forte presenca regional, o que pode ampliar referencias e oportunidades."
                        : "Mesmo com menor participacao, sua modalidade pode ser uma escolha estrategica se combinar com sua rotina.",
                "nivel", nivelPorPercentual(participacaoModalidade)
        ));

        return fallback;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> primeiroRegistro(Object valor) {
        if (valor instanceof List<?> lista && !lista.isEmpty() && lista.get(0) instanceof Map<?, ?>) {
            return (Map<String, Object>) lista.get(0);
        }
        return Map.of();
    }

    private Object valorOuPadrao(Object valor, Object padrao) {
        return valor != null ? valor : padrao;
    }

    private double matriculadosDaModalidade(List<Map<String, Object>> modalidades, int modalidade) {
        String modalidadeLabel = modalidade == 1 ? "Presencial" : "EAD";
        return modalidades.stream()
                .filter(item -> modalidadeLabel.equalsIgnoreCase(String.valueOf(item.get("modalidade"))))
                .findFirst()
                .map(item -> toDouble(item.get("total_matriculados")))
                .orElse(0.0);
    }

    private String nivelPorPercentual(double valor) {
        if (valor >= 70.0) return "alto";
        if (valor >= 35.0) return "medio";
        return "baixo";
    }

    private double toDouble(Object valor) {
        if (valor == null) return 0.0;
        if (valor instanceof Number) return ((Number) valor).doubleValue();
        try { return Double.parseDouble(valor.toString()); } catch (Exception e) { return 0.0; }
    }

    private String truncar(String texto, int max) {
        if (texto == null) return "";
        return texto.length() > max ? texto.substring(0, max) : texto;
    }

    private String validarNivel(String nivel) {
        if ("alto".equals(nivel) || "medio".equals(nivel) || "baixo".equals(nivel)) return nivel;
        return "medio";
    }
}
