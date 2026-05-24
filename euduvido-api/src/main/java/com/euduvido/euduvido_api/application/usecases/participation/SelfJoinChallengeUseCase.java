package com.euduvido.euduvido_api.application.usecases.participation;

import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;

public class SelfJoinChallengeUseCase {
    private final ChallengeParticipationRepository participationRepository;
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    public SelfJoinChallengeUseCase(ChallengeParticipationRepository participationRepository,
                                     ChallengeRepository challengeRepository,
                                     UserRepository userRepository) {
        this.participationRepository = participationRepository;
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
    }

    public ChallengeParticipation execute(Long userId, Long challengeId) {
        var existing = participationRepository.findByUserIdAndChallengeId(userId, challengeId);
        if (existing.isPresent()) {
            return existing.get();
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com id: " + userId));
        var challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Desafio não encontrado com id: " + challengeId));

        var participation = ChallengeParticipation.create(user, challenge);
        participation.accept();
        return participationRepository.save(participation);
    }
}
