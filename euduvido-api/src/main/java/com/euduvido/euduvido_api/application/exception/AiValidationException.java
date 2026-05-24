package com.euduvido.euduvido_api.application.exception;

import com.euduvido.euduvido_api.application.services.ValidationResult;

public class AiValidationException extends RuntimeException {
    private final ValidationResult result;

    public AiValidationException(ValidationResult result) {
        super(result.reason());
        this.result = result;
    }

    public ValidationResult getResult() { return result; }
}
