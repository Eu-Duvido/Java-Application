package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;

/**
 * Caso de uso: Criar uma nova participação de desafio no qual relaciona um usuáario a um desafio.
 * Responsabilidade: Validar dados da participação e persistir.
 */
public class CreateChallengeParticipationUseCase {
    private final ChallengeParticipationRepository challengeParticipationRepository;

    public CreateChallengeParticipationUseCase (ChallengeParticipationRepository challengeParticipationRepository) {
        this.challengeParticipationRepository = challengeParticipationRepository;
    }

    /**
     * Executa a criação de uma nova participação de desafio
     * @param user usuário participante
     * @param challenge desafio associado
     * @return Participação criada
     * @throws IllegalArgumentException se dados são inválidos
     */

    public ChallengeParticipation execute (User user, Challenge challenge) {
        // Criar participação (validações de domínio ocorrem aqui)
        ChallengeParticipation newParticipation = ChallengeParticipation.create(user, challenge);

        // Persistir participação
        return challengeParticipationRepository.save(newParticipation);
    }
}
