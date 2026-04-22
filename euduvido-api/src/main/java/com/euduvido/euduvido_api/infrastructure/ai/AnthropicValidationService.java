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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class AnthropicValidationService implements AiValidationService {

    private static final Logger log = LoggerFactory.getLogger(AnthropicValidationService.class);

    private final WebClient webClient;
    private final String model;
    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnthropicValidationService(
            @Value("${app.anthropic.base-url}") String baseUrl,
            @Value("${app.anthropic.api-key}") String apiKey,
            @Value("${app.anthropic.model}") String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("content-type", "application/json")
                .build();
    }

    @Override
    public ValidationResult validateChallenge(String title, String description, String subject) {
        if (apiKey == null || apiKey.isBlank()) {
            return bypass("API key não configurada");
        }
        try {
            String prompt = loadPrompt("prompts/validate_challenge.txt")
                    .replace("{{title}}", title)
                    .replace("{{description}}", description != null ? description : "")
                    .replace("{{subject}}", subject != null ? subject : "");

            String response = callAnthropic(List.of(Map.of("type", "text", "text", prompt)));
            return parseResult(response);
        } catch (Exception e) {
            log.warn("Falha na validação IA do desafio: {}", e.getMessage());
            return bypass("Validação IA indisponível");
        }
    }

    @Override
    public ValidationResult validateProofImage(byte[] imageBytes, String mimeType, String challengeTitle, String challengeDescription) {
        if (apiKey == null || apiKey.isBlank()) {
            return bypass("API key não configurada");
        }
        try {
            String prompt = loadPrompt("prompts/validate_proof.txt")
                    .replace("{{title}}", challengeTitle)
                    .replace("{{description}}", challengeDescription != null ? challengeDescription : "");

            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            List<Map<String, Object>> content = List.of(
                    Map.of("type", "image", "source", Map.of(
                            "type", "base64",
                            "media_type", mimeType,
                            "data", base64)),
                    Map.of("type", "text", "text", prompt)
            );

            String response = callAnthropic(content);
            return parseResult(response);
        } catch (Exception e) {
            log.warn("Falha na validação IA da prova: {}", e.getMessage());
            return bypass("Validação IA indisponível");
        }
    }

    @Override
    public ValidationResult validateProofLocation(Double latitude, Double longitude, String challengeTitle) {
        return bypass("Validação de localização não implementada via IA");
    }

    private String callAnthropic(List<Map<String, Object>> content) {
        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", 512,
                "messages", List.of(Map.of("role", "user", "content", content))
        );

        return webClient.post()
                .uri("/v1/messages")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private ValidationResult parseResult(String responseJson) throws Exception {
        JsonNode root = objectMapper.readTree(responseJson);
        String text = root.path("content").get(0).path("text").asText();

        // Extract JSON from response (may be wrapped in markdown code block)
        String jsonStr = text.trim();
        if (jsonStr.startsWith("```")) {
            jsonStr = jsonStr.replaceAll("```(?:json)?\\s*", "").replaceAll("```\\s*$", "").trim();
        }

        JsonNode result = objectMapper.readTree(jsonStr);
        boolean valid = result.path("valid").asBoolean(true);
        double confidence = result.path("confidence").asDouble(1.0);
        String reason = result.path("reason").asText("");

        List<String> issues = List.of();
        JsonNode issuesNode = result.path("issues");
        if (issuesNode.isArray()) {
            issues = objectMapper.convertValue(issuesNode, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, String.class));
        }

        return new ValidationResult(valid, confidence, reason, issues);
    }

    private ValidationResult bypass(String reason) {
        return new ValidationResult(true, 1.0, reason, List.of());
    }

    private String loadPrompt(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return resource.getContentAsString(StandardCharsets.UTF_8);
    }
}
