package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;

import java.util.List;

/**
 * Caso de uso: Listar desafios recebidos por um usuário.
 * Responsabilidade: Recuperar participações do repositório.
 */
public class ListReceivedChallengesUseCase {
    private final ChallengeParticipationRepository participationRepository;

    public ListReceivedChallengesUseCase(ChallengeParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    /**
     * Executa a listagem de desafios recebidos
     * @param userId ID do usuário
     * @return Lista de participações (desafios recebidos) do usuário
     */
    public List<ChallengeParticipation> execute(Long userId) {
        return participationRepository.findByUserId(userId);
    }
}

