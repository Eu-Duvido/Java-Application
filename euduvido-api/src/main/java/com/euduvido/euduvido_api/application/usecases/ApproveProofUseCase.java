package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.repositories.ProofRepository;

/**
 * Caso de uso: Aprovar comprovação de um desafio.
 * Responsabilidade: Marcar comprovação como aprovada e atualizar status da participação.
 */
public class ApproveProofUseCase {
    private final ProofRepository proofRepository;

    public ApproveProofUseCase(ProofRepository proofRepository) {
        this.proofRepository = proofRepository;
    }

    /**
     * Executa a aprovação de uma comprovação
     * @param proofId ID da comprovação
     * @return Comprovação aprovada
     * @throws IllegalArgumentException se comprovação não existe
     * @throws IllegalStateException se comprovação já foi aprovada
     */
    public Proof execute(Long proofId) {
        // Buscar comprovação
        Proof proof = proofRepository.findById(proofId)
                .orElseThrow(() -> new IllegalArgumentException("Comprovação não encontrada"));

        // Aprovar comprovação (validação de estado ocorre aqui)
        proof.approve();

        // Persistir comprovação atualizada
        return proofRepository.save(proof);
    }
}

