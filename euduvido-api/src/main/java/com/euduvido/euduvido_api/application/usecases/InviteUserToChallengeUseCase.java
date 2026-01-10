package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;

/**
 * Caso de uso: Convidar um usuário para participar de um desafio.
 * Responsabilidade: Criar uma participação com status INVITED.
 */
public class InviteUserToChallengeUseCase {
    private final ChallengeParticipationRepository participationRepository;
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    public InviteUserToChallengeUseCase(ChallengeParticipationRepository participationRepository,
                                       ChallengeRepository challengeRepository,
                                       UserRepository userRepository) {
        this.participationRepository = participationRepository;
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
    }

    /**
     * Executa o convite de um usuário para um desafio
     * @param userId ID do usuário a ser convidado
     * @param challengeId ID do desafio
     * @return Participação criada com status INVITED
     * @throws IllegalArgumentException se usuário ou desafio não existem
     */
    public ChallengeParticipation execute(Long userId, Long challengeId) {
        // Buscar usuário
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Buscar desafio
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Desafio não encontrado"));

        // Verificar se já existe participação
        if (participationRepository.findByUserIdAndChallengeId(userId, challengeId).isPresent()) {
            throw new IllegalArgumentException("Usuário já foi convidado para este desafio");
        }

        // Criar participação
        ChallengeParticipation participation = ChallengeParticipation.create(user, challenge);

        // Persistir participação
        return participationRepository.save(participation);
    }
}

