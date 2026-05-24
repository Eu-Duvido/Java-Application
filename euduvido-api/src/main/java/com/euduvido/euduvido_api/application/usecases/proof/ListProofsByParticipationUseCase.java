package com.euduvido.euduvido_api.application.usecases.proof;

import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.repositories.ProofRepository;

import java.util.List;

/**
 * Caso de uso: Listar comprovações de uma participação.
 */
public class ListProofsByParticipationUseCase {
    private final ProofRepository proofRepository;

    public ListProofsByParticipationUseCase(ProofRepository proofRepository) {
        this.proofRepository = proofRepository;
    }

    /**
     * Executa a listagem por participação
     * @param participationId ID da participação
     * @return Lista de comprovações
     */
    public List<Proof> execute(Long participationId) {
        return proofRepository.findByParticipationId(participationId);
    }
}

