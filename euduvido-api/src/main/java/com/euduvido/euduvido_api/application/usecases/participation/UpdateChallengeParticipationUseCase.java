package com.euduvido.euduvido_api.application.usecases.participation;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;

public class UpdateChallengeParticipationUseCase {
    private final ChallengeParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    public UpdateChallengeParticipationUseCase(ChallengeParticipationRepository participationRepository,
                                               UserRepository userRepository,
                                               ChallengeRepository challengeRepository) {
        this.participationRepository = participationRepository;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
    }

    public ChallengeParticipation execute(Long id, Long userId, Long challengeId, ParticipationStatus status) {
        ChallengeParticipation existing = participationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participação não encontrada com id: " + id));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com id: " + userId));
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Desafio não encontrado com id: " + challengeId));
        ChallengeParticipation updated = ChallengeParticipation.createFromDatabase(
                existing.getId(), user, challenge, status, existing.getProgress());
        return participationRepository.save(updated);
    }
}
