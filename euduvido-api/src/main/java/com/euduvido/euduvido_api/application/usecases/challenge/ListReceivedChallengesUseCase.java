package com.euduvido.euduvido_api.application.usecases.challenge;

import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import com.euduvido.euduvido_api.domain.pagination.PageResult;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;

import java.util.Optional;

public class ListReceivedChallengesUseCase {
    private final ChallengeParticipationRepository participationRepository;

    public ListReceivedChallengesUseCase(ChallengeParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    public PageResult<ChallengeParticipation> execute(Long userId, Optional<ParticipationStatus> status, int page, int size) {
        return participationRepository.findByUserIdPaged(userId, status, page, size);
    }
}