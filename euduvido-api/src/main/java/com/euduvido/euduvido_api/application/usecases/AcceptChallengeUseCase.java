package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;

/**
 * Caso de uso: Aceitar um convite de desafio.
 * Responsabilidade: Mudar status da participação para ACCEPTED.
 */
public class AcceptChallengeUseCase {
    private final ChallengeParticipationRepository participationRepository;

    public AcceptChallengeUseCase(ChallengeParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    /**
     * Executa a aceitação de um desafio
     * @param participationId ID da participação
     * @return Participação atualizada com status ACCEPTED
     * @throws IllegalArgumentException se participação não existe
     * @throws IllegalStateException se participação não está em status INVITED
     */
    public ChallengeParticipation execute(Long participationId) {
        // Buscar participação
        ChallengeParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("Participação não encontrada"));

        // Aceitar desafio (validação de status ocorre aqui)
        participation.accept();

        // Persistir participação atualizada
        return participationRepository.save(participation);
    }
}

