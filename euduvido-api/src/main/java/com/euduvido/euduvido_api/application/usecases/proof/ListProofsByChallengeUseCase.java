package com.euduvido.euduvido_api.application.usecases.proof;

import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.repositories.ProofRepository;

import java.util.List;

public class ListProofsByChallengeUseCase {
    private final ProofRepository proofRepository;

    public ListProofsByChallengeUseCase(ProofRepository proofRepository) {
        this.proofRepository = proofRepository;
    }

    public List<Proof> execute(Long challengeId) {
        return proofRepository.findByChallengeId(challengeId);
    }
}
