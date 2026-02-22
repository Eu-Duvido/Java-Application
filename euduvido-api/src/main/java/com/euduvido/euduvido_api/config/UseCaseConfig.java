package com.euduvido.euduvido_api.config;

import com.euduvido.euduvido_api.application.usecases.*;
import com.euduvido.euduvido_api.application.usecases.challenge.*;
import com.euduvido.euduvido_api.application.usecases.participation.CreateChallengeParticipationUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.DeleteChallengeParticipationUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.UpdateChallengeParticipationUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.ListChallengeParticipationUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.ApproveProofUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.SubmitProofUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.GetProofUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.ListProofsByParticipationUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.UpdateProofUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.DeleteProofUseCase;
import com.euduvido.euduvido_api.application.usecases.invite.CreateInviteUseCase;
import com.euduvido.euduvido_api.application.usecases.invite.AcceptInviteUseCase;
import com.euduvido.euduvido_api.application.usecases.invite.DeleteInviteUseCase;
import com.euduvido.euduvido_api.application.usecases.invite.ListInvitesUseCase;
import com.euduvido.euduvido_api.application.usecases.invite.GetInviteUseCase;
import com.euduvido.euduvido_api.application.usecases.user.CreateUserUseCase;
import com.euduvido.euduvido_api.application.usecases.user.DeleteUserUseCase;
import com.euduvido.euduvido_api.application.usecases.user.ListUserUseCase;
import com.euduvido.euduvido_api.application.usecases.user.UpdateUserUseCase;
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
    public DeleteChallengeUseCase deleteChallengeUseCase(ChallengeRepository challengeRepository) {
        return new DeleteChallengeUseCase(challengeRepository);
    }

    @Bean
    public ListChallengeUseCase listChallengeUseCase(ChallengeRepository challengeRepository) {
        return new ListChallengeUseCase(challengeRepository);
    }

    @Bean
    public UpdateChallengeUseCase updateChallengeUseCase(ChallengeRepository challengeRepository,
                                                         UserRepository userRepository) {
        return new UpdateChallengeUseCase(challengeRepository, userRepository);
    }

    @Bean
    public CreateChallengeParticipationUseCase createChallengeParticipationUseCase(ChallengeParticipationRepository challengeParticipationRepository) {
        return new CreateChallengeParticipationUseCase(challengeParticipationRepository);
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
    public GetProofUseCase getProofUseCase(ProofRepository proofRepository) {
        return new GetProofUseCase(proofRepository);
    }

    @Bean
    public ListProofsByParticipationUseCase listProofsByParticipationUseCase(ProofRepository proofRepository) {
        return new ListProofsByParticipationUseCase(proofRepository);
    }

    @Bean
    public UpdateProofUseCase updateProofUseCase(ProofRepository proofRepository) {
        return new UpdateProofUseCase(proofRepository);
    }

    @Bean
    public DeleteProofUseCase deleteProofUseCase(ProofRepository proofRepository) {
        return new DeleteProofUseCase(proofRepository);
    }

    @Bean
    public CreateInviteUseCase createInviteUseCase(com.euduvido.euduvido_api.domain.repositories.InviteRepository inviteRepository,
                                                   com.euduvido.euduvido_api.domain.repositories.UserRepository userRepository) {
        return new CreateInviteUseCase(inviteRepository, userRepository);
    }

    @Bean
    public AcceptInviteUseCase acceptInviteUseCase(com.euduvido.euduvido_api.domain.repositories.InviteRepository inviteRepository) {
        return new AcceptInviteUseCase(inviteRepository);
    }

    @Bean
    public DeleteInviteUseCase deleteInviteUseCase(com.euduvido.euduvido_api.domain.repositories.InviteRepository inviteRepository) {
        return new DeleteInviteUseCase(inviteRepository);
    }

    @Bean
    public ListInvitesUseCase listInvitesUseCase(com.euduvido.euduvido_api.domain.repositories.InviteRepository inviteRepository) {
        return new ListInvitesUseCase(inviteRepository);
    }

    @Bean
    public GetInviteUseCase getInviteUseCase(com.euduvido.euduvido_api.domain.repositories.InviteRepository inviteRepository) {
        return new GetInviteUseCase(inviteRepository);
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
    public DeleteChallengeParticipationUseCase deleteChallengeParticipationUseCase(
            ChallengeParticipationRepository participationRepository) {
        return new DeleteChallengeParticipationUseCase(participationRepository);
    }

    @Bean
    public UpdateExpiredChallengesUseCase updateExpiredChallengesUseCase(ChallengeRepository challengeRepository) {
        return new UpdateExpiredChallengesUseCase(challengeRepository);
    }

    @Bean
    public UpdateChallengeParticipationUseCase UpdateChallengeParticipationUseCase(ChallengeParticipationRepository participationRepository, UserRepository userRepository) {
        return new UpdateChallengeParticipationUseCase(participationRepository, userRepository);
    }

    @Bean
    public ListChallengeParticipationUseCase listChallengeParticipationUseCase(ChallengeParticipationRepository participationRepository) {
        return new ListChallengeParticipationUseCase(participationRepository);
    }
}
