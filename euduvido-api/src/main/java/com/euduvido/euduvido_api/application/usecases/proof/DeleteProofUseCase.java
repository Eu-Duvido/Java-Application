package com.euduvido.euduvido_api.application.usecases.proof;

import com.euduvido.euduvido_api.domain.repositories.ProofRepository;

/**
 * Caso de uso: Deletar uma comprovação.
 */
public class DeleteProofUseCase {
    private final ProofRepository proofRepository;

    public DeleteProofUseCase(ProofRepository proofRepository) {
        this.proofRepository = proofRepository;
    }

    /**
     * Executa a deleção
     * @param id ID da comprovação
     */
    public void execute(Long id) {
        // Validar existência
        proofRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comprovação não encontrada"));
        proofRepository.deleteById(id);
    }
}

