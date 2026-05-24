package com.euduvido.euduvido_api.application.usecases.participation;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;

/**
 * Caso de uso: Criar uma nova participação de desafio relacionando um usuário a um desafio.
 */
public class CreateChallengeParticipationUseCase {
    private final ChallengeParticipationRepository challengeParticipationRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    public CreateChallengeParticipationUseCase(ChallengeParticipationRepository challengeParticipationRepository,
                                               UserRepository userRepository,
                                               ChallengeRepository challengeRepository) {
        this.challengeParticipationRepository = challengeParticipationRepository;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
    }

    public ChallengeParticipation execute(Long userId, Long challengeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com id: " + userId));
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Desafio não encontrado com id: " + challengeId));
        return challengeParticipationRepository.save(ChallengeParticipation.create(user, challenge));
    }
}
