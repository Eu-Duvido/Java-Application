package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;

import java.util.List;

/**
 * Caso de uso: Atualizar status de desafios expirados.
 * Responsabilidade: Verificar desafios e expirar os que passaram do deadline.
 */
public class UpdateExpiredChallengesUseCase {
    private final ChallengeRepository challengeRepository;

    public UpdateExpiredChallengesUseCase(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    /**
     * Executa a atualização de desafios expirados
     * Verifica todos os desafios ativos e expira os que passaram do deadline
     * @return Lista de desafios que foram expirados
     */
    public List<Challenge> execute() {
        // Buscar desafios ativos
        List<Challenge> activeChallenges = challengeRepository.findByStatus(ChallengeStatus.ACTIVE);

        // Expirar desafios que passaram do deadline
        for (Challenge challenge : activeChallenges) {
            if (challenge.isExpired()) {
                challenge.expire();
                challengeRepository.save(challenge);
            }
        }

        return activeChallenges.stream()
                .filter(Challenge::isExpired)
                .toList();
    }
}

