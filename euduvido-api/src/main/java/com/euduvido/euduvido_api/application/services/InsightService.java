package com.euduvido.euduvido_api.application.services;

import com.euduvido.euduvido_api.infrastructure.persistence.entities.AiInsightEntity;
import com.euduvido.euduvido_api.infrastructure.persistence.repositories.AiInsightJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Serviço responsável por:
 *   1. Chamar a API Python (DW) para obter dados do INEP + prompt_sugerido
 *   2. Enviar os dados ao Gemini para geração dos insights
 *   3. Parsear a resposta do Gemini
 *   4. Salvar os insights na tabela ai_insights
 *
 * Configurações necessárias no application.yml:
 *   python.api.url=http://localhost:8000
 *   gemini.api.key=SUA_CHAVE_AQUI
 */
@Service
public class InsightService {

    @Value("${python.api.url:http://localhost:8000}")
    private String pythonApiUrl;

    @Value("${app.gemini.api-key}")
    private String geminiApiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AiInsightJpaRepository insightRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ─────────────────────────────────────────────────────────────────────────
    // Método principal
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Gera e salva os 3 insights personalizados para o perfil do usuário.
     *
     * @param curso       Nome do curso (ex.: "Direito")
     * @param regiao      Região do Brasil (ex.: "Nordeste")
     * @param modalidade  1 = Presencial | 2 = EAD
     * @param areaGeral   Área CINE geral — pode passar string vazia
     */
    public List<AiInsightEntity> gerarInsights(
            String curso,
            String regiao,
            int modalidade,
            String areaGeral) throws Exception {

        // 1. Busca dados do DW na API Python
        Map<String, Object> dadosDW = buscarDadosDW(curso, regiao, modalidade, areaGeral);

        // 2. Monta o prompt completo e envia ao Gemini
        String promptSugerido = (String) dadosDW.get("prompt_sugerido");
        String promptCompleto = promptSugerido
                + "\n\nDados do Censo INEP 2024:\n"
                + objectMapper.writeValueAsString(dadosDW);

        String respostaGemini = chamarGemini(promptCompleto);

        // 3. Parseia o JSON retornado pelo Gemini
        List<Map<String, Object>> insightsJson = objectMapper.readValue(
                limparJsonGemini(respostaGemini),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
        );

        // 4. Salva cada insight no banco
        List<AiInsightEntity> salvos = new ArrayList<>();
        for (Map<String, Object> ins : insightsJson) {
            AiInsightEntity entity = mapearInsight(ins, curso, regiao, modalidade, areaGeral);
            salvos.add(insightRepository.save(entity));
        }

        return salvos;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Chamada à API Python
    // ─────────────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private Map<String, Object> buscarDadosDW(
            String curso, String regiao, int modalidade, String areaGeral) {

        String url = pythonApiUrl + "/insights/dados-perfil"
                + "?no_curso="      + URLEncoder.encode(curso,  StandardCharsets.UTF_8)
                + "&no_regiao="     + URLEncoder.encode(regiao, StandardCharsets.UTF_8)
                + "&tp_modalidade=" + modalidade
                + "&no_area_geral=" + URLEncoder.encode(areaGeral != null ? areaGeral : "", StandardCharsets.UTF_8);

        return restTemplate.getForObject(url, Map.class);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Chamada ao Gemini
    // ─────────────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private String chamarGemini(String prompt) throws Exception {
        String geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/"
                + "gemini-1.5-flash:generateContent?key=" + geminiApiKey;

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                ))
        );

        Map<String, Object> resposta = restTemplate.postForObject(geminiUrl, body, Map.class);

        // Extrai o texto da resposta do Gemini
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) resposta.get("candidates");
        Map<String, Object> content          = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts      = (List<Map<String, Object>>) content.get("parts");
        return (String) parts.get(0).get("text");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Remove blocos ```json ... ``` caso o Gemini os inclua na resposta. */
    private String limparJsonGemini(String texto) {
        return texto
                .replaceAll("(?s)```json\\s*", "")
                .replaceAll("```", "")
                .trim();
    }

    private AiInsightEntity mapearInsight(
            Map<String, Object> ins,
            String curso, String regiao, int modalidade, String areaGeral) {

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
