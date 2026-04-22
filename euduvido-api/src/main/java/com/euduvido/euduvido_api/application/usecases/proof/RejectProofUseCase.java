package com.euduvido.euduvido_api.application.usecases.proof;

import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.repositories.ProofRepository;

/** Caso de uso: Rejeitar uma comprovação com motivo. */
public class RejectProofUseCase {
    private final ProofRepository proofRepository;

    public RejectProofUseCase(ProofRepository proofRepository) {
        this.proofRepository = proofRepository;
    }

    public Proof execute(Long proofId, String reason) {
        Proof proof = proofRepository.findById(proofId)
                .orElseThrow(() -> new IllegalArgumentException("Comprovação não encontrada"));
        proof.reject(reason);
        return proofRepository.save(proof);
    }
}
