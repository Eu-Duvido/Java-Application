package com.euduvido.euduvido_api.config;

import com.euduvido.euduvido_api.application.usecases.*;
import com.euduvido.euduvido_api.domain.repositories.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de injeção de dependência para casos de uso.
 * Define como os casos de uso devem ser instanciados com suas dependências.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public CreateUserUseCase createUserUseCase(UserRepository userRepository) {
        return new CreateUserUseCase(userRepository);
    }

    @Bean
    public ListUserUseCase listUserUseCase(UserRepository userRepository) {
        return new ListUserUseCase(userRepository);
    }

    @Bean
    public UpdateUserUseCase updateUserUseCase(UserRepository userRepository) {
        return new UpdateUserUseCase(userRepository);
    }

    @Bean
    public DeleteUserUseCase deleteUserUseCase(UserRepository userRepository) {
        return new DeleteUserUseCase(userRepository);
    }

    @Bean
    public CreateChallengeUseCase createChallengeUseCase(ChallengeRepository challengeRepository,
                                                        UserRepository userRepository) {
        return new CreateChallengeUseCase(challengeRepository, userRepository);
    }

    @Bean
    public InviteUserToChallengeUseCase inviteUserToChallengeUseCase(
            ChallengeParticipationRepository participationRepository,
            ChallengeRepository challengeRepository,
            UserRepository userRepository) {
        return new InviteUserToChallengeUseCase(participationRepository, challengeRepository, userRepository);
    }

    @Bean
    public AcceptChallengeUseCase acceptChallengeUseCase(ChallengeParticipationRepository participationRepository) {
        return new AcceptChallengeUseCase(participationRepository);
    }

    @Bean
    public RefuseChallengeUseCase refuseChallengeUseCase(ChallengeParticipationRepository participationRepository) {
        return new RefuseChallengeUseCase(participationRepository);
    }

    @Bean
    public SubmitProofUseCase submitProofUseCase(ProofRepository proofRepository,
                                                 ChallengeParticipationRepository participationRepository) {
        return new SubmitProofUseCase(proofRepository, participationRepository);
    }

    @Bean
    public ApproveProofUseCase approveProofUseCase(ProofRepository proofRepository) {
        return new ApproveProofUseCase(proofRepository);
    }

    @Bean
    public ListCreatedChallengesUseCase listCreatedChallengesUseCase(ChallengeRepository challengeRepository) {
        return new ListCreatedChallengesUseCase(challengeRepository);
    }

    @Bean
    public ListReceivedChallengesUseCase listReceivedChallengesUseCase(
            ChallengeParticipationRepository participationRepository) {
        return new ListReceivedChallengesUseCase(participationRepository);
    }

    @Bean
    public UpdateExpiredChallengesUseCase updateExpiredChallengesUseCase(ChallengeRepository challengeRepository) {
        return new UpdateExpiredChallengesUseCase(challengeRepository);
    }
}

