package com.euduvido.euduvido_api.application.usecases.challenge;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;

import java.util.List;

/**
 * Caso de uso: Listar desafios criados por um usuário.
 * Responsabilidade: Recuperar desafios do repositório.
 */
public class ListCreatedChallengesUseCase {
    private final ChallengeRepository challengeRepository;

    public ListCreatedChallengesUseCase(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    /**
     * Executa a listagem de desafios criados
     * @param userId ID do usuário criador
     * @return Lista de desafios criados pelo usuário
     */
    public List<Challenge> execute(Long userId) {
        return challengeRepository.findByCreatorId(userId);
    }
}

