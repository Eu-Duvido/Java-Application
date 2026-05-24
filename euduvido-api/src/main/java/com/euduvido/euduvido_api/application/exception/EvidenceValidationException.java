package com.euduvido.euduvido_api.application.exception;

import com.euduvido.euduvido_api.application.services.EvidenceValidationResult;

public class EvidenceValidationException extends RuntimeException {
    private final EvidenceValidationResult result;

    public EvidenceValidationException(EvidenceValidationResult result) {
        super(result.message());
        this.result = result;
    }

    public EvidenceValidationResult getResult() { return result; }
}
