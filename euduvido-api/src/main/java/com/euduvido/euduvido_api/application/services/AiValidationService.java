package com.euduvido.euduvido_api.application.services;

public interface AiValidationService {
    ValidationResult validateChallenge(String title, String description, String subject);
    EvidenceValidationResult validateProofImage(byte[] imageBytes, String mimeType, String challengeTitle, String challengeDescription);
    ValidationResult validateProofLocation(Double latitude, Double longitude, String challengeTitle);
    EvidenceValidationResult validateEvidenceContent(String content);
}
