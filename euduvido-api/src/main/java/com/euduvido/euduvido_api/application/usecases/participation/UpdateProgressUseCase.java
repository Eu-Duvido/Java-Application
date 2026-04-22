package com.euduvido.euduvido_api.application.usecases.participation;

import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;

/** Caso de uso: Atualizar o progresso de uma participação aceita. */
public class UpdateProgressUseCase {
    private final ChallengeParticipationRepository participationRepository;

    public UpdateProgressUseCase(ChallengeParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    public ChallengeParticipation execute(Long participationId, Integer progress) {
        ChallengeParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("Participação não encontrada"));
        participation.updateProgress(progress);
        return participationRepository.save(participation);
    }
}
