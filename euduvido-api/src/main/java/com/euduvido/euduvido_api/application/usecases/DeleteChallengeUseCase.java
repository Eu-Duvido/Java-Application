package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;

import java.util.Optional;

/**
 * Caso de uso: Deletar um desafio.
 * Responsabilidade: Deletar um desafio.
 */
public class DeleteChallengeUseCase {
    private final ChallengeRepository challengeRepository;

    public DeleteChallengeUseCase(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    /**
     * Executa a deleção de um novo desafio
     * @param id do desafio
     * @return void
     * @throws IllegalArgumentException se dados são inválidos
     */

    public void execute(Long id) {
        // Verifica se o desafio existe
        Optional<Challenge> challengeOpt = challengeRepository.findById(id);

        if (challengeOpt.isEmpty()) {
            throw new IllegalArgumentException("Desafio não encontrado");
        }

        challengeRepository.deleteById(id);
    }
}
