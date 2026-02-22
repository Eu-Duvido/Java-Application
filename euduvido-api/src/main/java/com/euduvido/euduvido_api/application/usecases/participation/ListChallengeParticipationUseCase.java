package com.euduvido.euduvido_api.application.usecases.participation;

import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;

import java.util.List;

/**
 * Caso de uso: Listar participações de desafios.
 * Fornece métodos de conveniência para listar por usuário, por desafio ou por status.
 */
public class ListChallengeParticipationUseCase {
    private final ChallengeParticipationRepository participationRepository;

    public ListChallengeParticipationUseCase(ChallengeParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    /**
     * Lista participações de um usuário
     * @param userId ID do usuário
     * @return lista de participações do usuário
     */
    public List<ChallengeParticipation> executeByUserId(Long userId) {
        return participationRepository.findByUserId(userId);
    }

    /**
     * Lista participações de um desafio
     * @param challengeId ID do desafio
     * @return lista de participações do desafio
     */
    public List<ChallengeParticipation> executeByChallengeId(Long challengeId) {
        return participationRepository.findByChallengeId(challengeId);
    }

    /**
     * Lista participações por status
     * @param status status de participação
     * @return lista de participações com o status informado
     */
    public List<ChallengeParticipation> executeByStatus(ParticipationStatus status) {
        return participationRepository.findByStatus(status);
    }
}

