package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;

import java.util.List;

/**
 * Caso de uso: Listar todos os desafios.
 * Responsabilidade: Listar todos os desafios.
 */
public class ListChallengeUseCase {
    private final ChallengeRepository challengeRepository;

    public ListChallengeUseCase(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    /**
     * Executa a listagem de todos os desafios
     * @return List<Challenge>
     */

    public List<Challenge> execute() {
        return challengeRepository.findAll();
    }
}
