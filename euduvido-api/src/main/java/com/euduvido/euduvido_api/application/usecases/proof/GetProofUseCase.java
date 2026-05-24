package com.euduvido.euduvido_api.application.usecases.proof;

import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.repositories.ProofRepository;

/**
 * Caso de uso: Buscar uma comprovação pelo ID.
 */
public class GetProofUseCase {
    private final ProofRepository proofRepository;

    public GetProofUseCase(ProofRepository proofRepository) {
        this.proofRepository = proofRepository;
    }

    /**
     * Executa a busca de uma comprovação
     * @param id ID da comprovação
     * @return Comprovação encontrada
     * @throws IllegalArgumentException se não existir
     */
    public Proof execute(Long id) {
        return proofRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comprovação não encontrada"));
    }
}

