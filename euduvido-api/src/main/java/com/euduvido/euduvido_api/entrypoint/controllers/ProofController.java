package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.usecases.ApproveProofUseCase;
import com.euduvido.euduvido_api.entrypoint.dtos.response.ProofResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para gerenciar comprovações.
 * Responsabilidade: Receber requisições HTTP e delegar para casos de uso.
 */
@RestController
@RequestMapping("/api/v1/proofs")
public class ProofController {
    private final ApproveProofUseCase approveProofUseCase;

    public ProofController(ApproveProofUseCase approveProofUseCase) {
        this.approveProofUseCase = approveProofUseCase;
    }

    /**
     * POST /api/v1/proofs/{id}/approve
     * Aprovar uma comprovação
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<ProofResponse> approveProof(@PathVariable Long id) {
        var proof = approveProofUseCase.execute(id);
        return ResponseEntity.ok(ProofResponse.fromDomain(proof));
    }
}

