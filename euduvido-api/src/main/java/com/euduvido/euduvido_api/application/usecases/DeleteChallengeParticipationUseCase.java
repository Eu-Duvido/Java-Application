package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;

/**
 * Caso de uso: Deletar participação de desafio de um usuário.
 * Responsabilidade: Deletar participação do repositório.
 */
public class DeleteChallengeParticipationUseCase {
    private final ChallengeParticipationRepository participationRepository;

    public DeleteChallengeParticipationUseCase(ChallengeParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    /**
     * Executa a deleção de participação de desafio
     * @param participationId ID da participação a ser deletada
     */
    public void execute(Long participationId) {
        participationRepository.deleteById(participationId);
    }
}
