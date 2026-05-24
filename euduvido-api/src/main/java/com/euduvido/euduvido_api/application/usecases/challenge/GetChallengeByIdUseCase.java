package com.euduvido.euduvido_api.application.usecases.challenge;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;

/**
 * Caso de uso: Buscar um desafio pelo ID.
 */
public class GetChallengeByIdUseCase {
    private final ChallengeRepository challengeRepository;

    public GetChallengeByIdUseCase(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    public Challenge execute(Long id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Desafio não encontrado"));
    }
}
