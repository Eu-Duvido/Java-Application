package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;

/**
 * Caso de uso: Recusar um convite de desafio.
 * Responsabilidade: Mudar status da participação para REFUSED.
 */
public class RefuseChallengeUseCase {
    private final ChallengeParticipationRepository participationRepository;

    public RefuseChallengeUseCase(ChallengeParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    /**
     * Executa a recusa de um desafio
     * @param participationId ID da participação
     * @return Participação atualizada com status REFUSED
     * @throws IllegalArgumentException se participação não existe
     * @throws IllegalStateException se participação não está em status INVITED
     */
    public ChallengeParticipation execute(Long participationId) {
        // Buscar participação
        ChallengeParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("Participação não encontrada"));

        // Recusar desafio (validação de status ocorre aqui)
        participation.refuse();

        // Persistir participação atualizada
        return participationRepository.save(participation);
    }
}

