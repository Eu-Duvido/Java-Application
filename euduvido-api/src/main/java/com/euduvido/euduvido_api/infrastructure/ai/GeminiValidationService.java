package com.euduvido.euduvido_api.infrastructure.ai;

import com.euduvido.euduvido_api.application.services.AiValidationService;
import com.euduvido.euduvido_api.application.services.ValidationResult;
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
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class GeminiValidationService implements AiValidationService {

    private static final Logger log = LoggerFactory.getLogger(GeminiValidationService.class);
    private static final int MAX_INLINE_BYTES = 15 * 1024 * 1024; // 15 MB — Gemini inline limit

    private final WebClient webClient;
    private final String model;
    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiValidationService(
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

    // ─── Public interface ────────────────────────────────────────────────────────

    @Override
    public ValidationResult validateChallenge(String title, String description, String subject) {
        if (apiKey == null || apiKey.isBlank()) {
            return bypass("API key não configurada");
        }
        try {
            String systemInstruction = loadPrompt("prompts/validate_challenge.txt");
            String userMessage = "Title: " + title + "\n" +
                    "Description: " + (description != null ? description : "(none)") + "\n" +
                    "Subject/Category: " + (subject != null ? subject : "(none)");

            List<Map<String, Object>> parts = List.of(Map.of("text", userMessage));
            String response = callGemini(parts, systemInstruction, challengeSchema());
            return parseResult(response);
        } catch (WebClientResponseException e) {
            log.error("Gemini API error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return bypass("Validação IA indisponível");
        } catch (Exception e) {
            log.warn("Falha na validação IA do desafio: {}", e.getMessage());
            return bypass("Validação IA indisponível");
        }
    }

    @Override
    public ValidationResult validateProofImage(byte[] fileBytes, String mimeType, String challengeTitle, String challengeDescription) {
        if (apiKey == null || apiKey.isBlank()) {
            return bypass("API key não configurada");
        }
        if (fileBytes.length > MAX_INLINE_BYTES) {
            log.warn("Arquivo de {} bytes excede o limite inline do Gemini ({})", fileBytes.length, MAX_INLINE_BYTES);
            return bypass("Arquivo muito grande para validação automática");
        }
        try {
            String systemInstruction = loadPrompt("prompts/validate_proof.txt");
            String userMessage = "Challenge Title: " + challengeTitle + "\n" +
                    "Challenge Description: " + (challengeDescription != null ? challengeDescription : "(none)");

            String base64 = Base64.getEncoder().encodeToString(fileBytes);
            List<Map<String, Object>> parts = List.of(
                    Map.of("inlineData", Map.of("mimeType", mimeType, "data", base64)),
                    Map.of("text", userMessage)
            );

            String response = callGemini(parts, systemInstruction, proofSchema());
            return parseResult(response);
        } catch (WebClientResponseException e) {
            log.error("Gemini API error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return bypass("Validação IA indisponível");
        } catch (Exception e) {
            log.warn("Falha na validação IA da prova: {}", e.getMessage());
            return bypass("Validação IA indisponível");
        }
    }

    @Override
    public ValidationResult validateProofLocation(Double latitude, Double longitude, String challengeTitle) {
        return bypass("Validação de localização não implementada via IA");
    }

    // ─── Gemini HTTP call ────────────────────────────────────────────────────────

    private String callGemini(List<Map<String, Object>> parts, String systemInstruction,
                               Map<String, Object> responseSchema) {
        Map<String, Object> generationConfig = new LinkedHashMap<>();
        generationConfig.put("temperature", 0.0);
        generationConfig.put("maxOutputTokens", 1024);
        generationConfig.put("responseMimeType", "application/json");
        generationConfig.put("responseSchema", responseSchema);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("systemInstruction", Map.of("parts", List.of(Map.of("text", systemInstruction))));
        body.put("contents", List.of(Map.of("role", "user", "parts", parts)));
        body.put("generationConfig", generationConfig);

        return webClient.post()
                .uri(uri -> uri
                        .path("/v1beta/models/" + model + ":generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block();
    }

    // ─── Response parsing ────────────────────────────────────────────────────────

    private ValidationResult parseResult(String responseJson) throws Exception {
        JsonNode root = objectMapper.readTree(responseJson);

        // Gemini response: candidates[0].content.parts[0].text
        JsonNode candidates = root.path("candidates");
        if (!candidates.isArray() || candidates.isEmpty()) {
            String blockReason = root.path("promptFeedback").path("blockReason").asText("unknown");
            log.warn("Gemini retornou sem candidatos. blockReason={}", blockReason);
            return bypass("Resposta bloqueada pelo Gemini: " + blockReason);
        }

        String text = candidates.get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();
        log.debug("Gemini raw response: {}", text);

        // With responseMimeType=application/json, text is guaranteed to be valid JSON
        JsonNode result = objectMapper.readTree(text);
        boolean valid = result.path("valid").asBoolean(false);
        double confidence = result.path("confidence").asDouble(0.0);
        String reason = result.path("reason").asText("Sem motivo retornado pela IA");
        List<String> issues = extractStringList(result, "issues");

        return new ValidationResult(valid, confidence, reason, issues);
    }

    // ─── Response schemas (enforced server-side by Gemini) ───────────────────────

    private Map<String, Object> challengeSchema() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("valid", Map.of("type", "BOOLEAN"));
        properties.put("confidence", Map.of("type", "NUMBER"));
        properties.put("reason", Map.of("type", "STRING"));
        properties.put("issues", Map.of("type", "ARRAY", "items", Map.of("type", "STRING")));

        return Map.of(
                "type", "OBJECT",
                "required", List.of("valid", "confidence", "reason", "issues"),
                "properties", properties
        );
    }

    private Map<String, Object> proofSchema() {
        Map<String, Object> arrayOfStrings = Map.of("type", "ARRAY", "items", Map.of("type", "STRING"));

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("valid", Map.of("type", "BOOLEAN"));
        properties.put("confidence", Map.of("type", "NUMBER"));
        properties.put("reason", Map.of("type", "STRING"));
        properties.put("group_a", arrayOfStrings);
        properties.put("group_b", arrayOfStrings);
        properties.put("issues", arrayOfStrings);

        return Map.of(
                "type", "OBJECT",
                "required", List.of("valid", "confidence", "reason", "group_a", "group_b", "issues"),
                "properties", properties
        );
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    private List<String> extractStringList(JsonNode node, String field) {
        JsonNode arr = node.path(field);
        if (arr.isArray() && !arr.isEmpty()) {
            return objectMapper.convertValue(arr,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        }
        return List.of();
    }

    private ValidationResult bypass(String reason) {
        return new ValidationResult(true, 1.0, reason, List.of());
    }

    private String loadPrompt(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return resource.getContentAsString(StandardCharsets.UTF_8);
    }
}
