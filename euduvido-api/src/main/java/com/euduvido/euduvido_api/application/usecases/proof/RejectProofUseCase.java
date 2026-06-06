package com.euduvido.euduvido_api.application.usecases.proof;

import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.repositories.ProofRepository;

public class RejectProofUseCase {
    private final ProofRepository proofRepository;

    public RejectProofUseCase(ProofRepository proofRepository) {
        this.proofRepository = proofRepository;
    }

    public Proof execute(Long proofId, Long rejecterId, String reason) {
        Proof proof = proofRepository.findById(proofId)
                .orElseThrow(() -> new IllegalArgumentException("Comprovação não encontrada"));
        proof.reject(rejecterId, reason);
        return proofRepository.save(proof);
    }
}
