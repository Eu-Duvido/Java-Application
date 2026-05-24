package com.euduvido.euduvido_api.application.usecases.participation;

import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import com.euduvido.euduvido_api.domain.pagination.PageResult;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;

/** Lista convites enviados pelo criador de desafios (participações com status INVITED). */
public class ListSentInvitesUseCase {

    private final ChallengeParticipationRepository participationRepository;

    public ListSentInvitesUseCase(ChallengeParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    public PageResult<ChallengeParticipation> execute(Long creatorId, int page, int size) {
        return participationRepository.findByChallengeCreatorIdAndStatusPaged(
                creatorId, ParticipationStatus.INVITED, page, size);
    }
}
