package com.euduvido.euduvido_api.application.usecases.participation;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;

/**
 * Cria (ou retorna existente) uma participação ACCEPTED para o próprio criador do desafio.
 * Necessário para desafios criados antes da criação automática de participação.
 */
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
        return participationRepository
                .findByUserIdAndChallengeId(userId, challengeId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
                    Challenge challenge = challengeRepository.findById(challengeId)
                            .orElseThrow(() -> new IllegalArgumentException("Desafio não encontrado"));
                    return participationRepository.save(ChallengeParticipation.createForCreator(user, challenge));
                });
    }
}
