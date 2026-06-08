package com.euduvido.euduvido_api.infrastructure.ai;

import com.euduvido.euduvido_api.application.services.AiInsightsService;
import com.euduvido.euduvido_api.application.services.DashboardInsightsInput;
import com.euduvido.euduvido_api.entrypoint.dtos.response.AiInsight;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class GeminiInsightsService implements AiInsightsService {

    private static final Logger log = LoggerFactory.getLogger(GeminiInsightsService.class);

    private final WebClient webClient;
    private final String model;
    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiInsightsService(
            @Value("${app.gemini.base-url}") String baseUrl,
            @Value("${app.gemini.api-key}") String apiKey,
            @Value("${app.gemini.model}") String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    public List<AiInsight> generateDashboardInsights(DashboardInsightsInput input) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Gemini API key não configurada — retornando lista vazia de insights");
            return List.of();
        }
        try {
            String systemInstruction = loadPrompt("prompts/generate_insights.txt");
            String userMessage = buildUserMessage(input);

            List<Map<String, Object>> parts = List.of(Map.of("text", userMessage));
            String response = callGemini(parts, systemInstruction);
            return parseInsights(response);
        } catch (WebClientResponseException e) {
            log.error("Gemini API error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return List.of();
        } catch (Exception e) {
            log.warn("Falha ao gerar insights educacionais com IA: {}", e.getMessage());
            return List.of();
        }
    }

    // ─── Gemini HTTP call ────────────────────────────────────────────────────────

    private String callGemini(List<Map<String, Object>> parts, String systemInstruction) {
        Map<String, Object> generationConfig = new LinkedHashMap<>();
        generationConfig.put("temperature", 0.4);
        generationConfig.put("maxOutputTokens", 2048);
        generationConfig.put("responseMimeType", "application/json");
        generationConfig.put("responseSchema", insightsSchema());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("systemInstruction", Map.of("parts", List.of(Map.of("text", systemInstruction))));
        body.put("contents", List.of(Map.of("role", "user", "parts", parts)));
        body.put("generationConfig", generationConfig);

        return webClient.post()
                .uri(uri -> uri
                        .path("/v1beta/models/" + model + ":generateContent")
                        .build())
                .header("X-goog-api-key", apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block();
    }

    // ─── Response schema (enforced server-side by Gemini) ────────────────────────

    private Map<String, Object> insightsSchema() {
        Map<String, Object> insightProperties = new LinkedHashMap<>();
        insightProperties.put("icon",     Map.of("type", "STRING"));
        insightProperties.put("label",    Map.of("type", "STRING"));
        insightProperties.put("text",     Map.of("type", "STRING"));
        insightProperties.put("category", Map.of("type", "STRING"));
        insightProperties.put("color",    Map.of("type", "STRING"));

        Map<String, Object> insightSchema = new LinkedHashMap<>();
        insightSchema.put("type",       "OBJECT");
        insightSchema.put("required",   List.of("icon", "label", "text", "category", "color"));
        insightSchema.put("properties", insightProperties);

        Map<String, Object> insightsArray = new LinkedHashMap<>();
        insightsArray.put("type",  "ARRAY");
        insightsArray.put("items", insightSchema);

        Map<String, Object> rootProperties = new LinkedHashMap<>();
        rootProperties.put("insights", insightsArray);

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("type",       "OBJECT");
        root.put("required",   List.of("insights"));
        root.put("properties", rootProperties);
        return root;
    }

    // ─── Response parsing ────────────────────────────────────────────────────────

    private List<AiInsight> parseInsights(String responseJson) throws Exception {
        JsonNode root = objectMapper.readTree(responseJson);

        JsonNode candidates = root.path("candidates");
        if (!candidates.isArray() || candidates.isEmpty()) {
            String blockReason = root.path("promptFeedback").path("blockReason").asText("unknown");
            log.warn("Gemini retornou sem candidatos. blockReason={}", blockReason);
            return List.of();
        }

        String text = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
        log.debug("Gemini insights raw response: {}", text);

        JsonNode parsed = objectMapper.readTree(text);
        JsonNode insightsNode = parsed.path("insights");

        if (!insightsNode.isArray()) {
            log.warn("Campo 'insights' ausente ou não é array na resposta do Gemini");
            return List.of();
        }

        List<AiInsight> result = new ArrayList<>();
        for (JsonNode node : insightsNode) {
            String icon     = node.path("icon").asText("💡");
            String label    = node.path("label").asText("");
            String insText  = node.path("text").asText("");
            String category = node.path("category").asText("performance");
            String color    = node.path("color").asText("#2196F3");
            if (!label.isBlank() && !insText.isBlank()) {
                result.add(new AiInsight(icon, label, insText, category, color));
            }
        }
        return result;
    }

    // ─── User message builder ────────────────────────────────────────────────────

    private String buildUserMessage(DashboardInsightsInput input) throws Exception {
        Map<String, Object> app = new LinkedHashMap<>();
        app.put("totalUsers",                    input.totalUsers());
        app.put("activeUsers",                   input.activeUsers());
        app.put("retentionRate_pct",             input.retentionRate());
        app.put("evidenceApprovalRate_pct",      input.approvalRate());
        app.put("approvedProofs",                input.approvedProofs());
        app.put("totalProofs",                   input.totalProofs());
        app.put("activeChallenges",              input.activeChallenges());
        app.put("topSubject",                    input.topSubject());
        app.put("challengesWithParticipants",    input.totalChallengesWithParticipants());
        app.put("topChallenges",                 input.topChallenges());

        Map<String, Object> inep = new LinkedHashMap<>();
        inep.put("anoCenso",                     input.anoCenso());
        inep.put("totalIngressantes",            input.totalIngressantes());
        inep.put("pctIngFeminino",               input.pctIngFeminino());
        inep.put("pctIngMasculino",              input.pctIngMasculino());
        inep.put("taxaConclusaoPct",             input.taxaConclusaoPct());
        inep.put("pctEnem",                      input.pctEnem());
        inep.put("pct1824_faixaEtaria",          input.pct1824());
        inep.put("pctEad",                       input.pctEad());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("app",  app);
        payload.put("inep", inep);

        return objectMapper.writeValueAsString(payload);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    private String loadPrompt(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return resource.getContentAsString(StandardCharsets.UTF_8);
    }
}
