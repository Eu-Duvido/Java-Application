package com.euduvido.euduvido_api.application.usecases.proof;

import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;
import com.euduvido.euduvido_api.domain.repositories.ProofRepository;

public class ApproveProofUseCase {
    private final ProofRepository proofRepository;
    private final ChallengeParticipationRepository participationRepository;

    public ApproveProofUseCase(ProofRepository proofRepository,
                               ChallengeParticipationRepository participationRepository) {
        this.proofRepository = proofRepository;
        this.participationRepository = participationRepository;
    }

    public Proof execute(Long proofId, Long approverId) {
        Proof proof = proofRepository.findById(proofId)
                .orElseThrow(() -> new IllegalArgumentException("Comprovação não encontrada"));
        proof.approve(approverId);
        participationRepository.save(proof.getParticipation());
        return proofRepository.save(proof);
    }
}
