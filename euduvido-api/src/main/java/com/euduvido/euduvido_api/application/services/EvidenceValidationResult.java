package com.euduvido.euduvido_api.application.services;

public record EvidenceValidationResult(int status, String category, String message, boolean isValid) {}
