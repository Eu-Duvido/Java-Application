package com.euduvido.euduvido_api.application.services;

import java.util.List;

public record ValidationResult(boolean valid, double confidence, String reason, List<String> issues) {}
