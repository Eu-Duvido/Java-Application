package com.euduvido.euduvido_api.config;

import com.euduvido.euduvido_api.application.services.AiValidationService;
import com.euduvido.euduvido_api.application.usecases.*;
import com.euduvido.euduvido_api.application.usecases.challenge.*;
import com.euduvido.euduvido_api.application.usecases.participation.CreateChallengeParticipationUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.DeleteChallengeParticipationUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.SelfJoinChallengeUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.UpdateChallengeParticipationUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.ListChallengeParticipationUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.ApproveProofUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.SubmitProofUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.GetProofUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.ListProofsByParticipationUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.ListProofsByChallengeUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.UpdateProofUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.DeleteProofUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.ListSentInvitesUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.UpdateProgressUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.RejectProofUseCase;
import com.euduvido.euduvido_api.application.services.TokenService;
import com.euduvido.euduvido_api.application.usecases.auth.LoginUseCase;
import com.euduvido.euduvido_api.application.usecases.user.CreateUserUseCase;
import com.euduvido.euduvido_api.application.usecases.user.DeleteUserUseCase;
import com.euduvido.euduvido_api.application.usecases.user.ListUserUseCase;
import com.euduvido.euduvido_api.application.usecases.user.UpdateUserUseCase;
import com.euduvido.euduvido_api.domain.repositories.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração de injeção de dependência para casos de uso.
 * Define como os casos de uso devem ser instanciados com suas dependências.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public CreateUserUseCase createUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new CreateUserUseCase(userRepository, passwordEncoder);
    }

    @Bean
    public LoginUseCase loginUseCase(UserRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     TokenService tokenService) {
        return new LoginUseCase(userRepository, passwordEncoder, tokenService);
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
                                                         UserRepository userRepository,
                                                         AiValidationService aiValidationService) {
        return new CreateChallengeUseCase(challengeRepository, userRepository, aiValidationService);
    }

    @Bean
    public DeleteChallengeUseCase deleteChallengeUseCase(ChallengeRepository challengeRepository) {
        return new DeleteChallengeUseCase(challengeRepository);
    }

    @Bean
    public GetChallengeByIdUseCase getChallengeByIdUseCase(ChallengeRepository challengeRepository) {
        return new GetChallengeByIdUseCase(challengeRepository);
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
    public CreateChallengeParticipationUseCase createChallengeParticipationUseCase(
            ChallengeParticipationRepository challengeParticipationRepository,
            UserRepository userRepository,
            ChallengeRepository challengeRepository) {
        return new CreateChallengeParticipationUseCase(challengeParticipationRepository, userRepository, challengeRepository);
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
                                                 ChallengeParticipationRepository participationRepository,
                                                 com.euduvido.euduvido_api.application.services.FileStorageService fileStorageService,
                                                 AiValidationService aiValidationService) {
        return new SubmitProofUseCase(proofRepository, participationRepository, fileStorageService, aiValidationService);
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
    public ListProofsByChallengeUseCase listProofsByChallengeUseCase(ProofRepository proofRepository) {
        return new ListProofsByChallengeUseCase(proofRepository);
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
    public UpdateChallengeParticipationUseCase updateChallengeParticipationUseCase(
            ChallengeParticipationRepository participationRepository,
            UserRepository userRepository,
            ChallengeRepository challengeRepository) {
        return new UpdateChallengeParticipationUseCase(participationRepository, userRepository, challengeRepository);
    }

    @Bean
    public ListChallengeParticipationUseCase listChallengeParticipationUseCase(ChallengeParticipationRepository participationRepository) {
        return new ListChallengeParticipationUseCase(participationRepository);
    }

    @Bean
    public UpdateProgressUseCase updateProgressUseCase(ChallengeParticipationRepository participationRepository) {
        return new UpdateProgressUseCase(participationRepository);
    }

    @Bean
    public RejectProofUseCase rejectProofUseCase(ProofRepository proofRepository) {
        return new RejectProofUseCase(proofRepository);
    }

    @Bean
    public ListSentInvitesUseCase listSentInvitesUseCase(ChallengeParticipationRepository participationRepository) {
        return new ListSentInvitesUseCase(participationRepository);
    }

    @Bean
    public SelfJoinChallengeUseCase selfJoinChallengeUseCase(
            ChallengeParticipationRepository participationRepository,
            ChallengeRepository challengeRepository,
            UserRepository userRepository) {
        return new SelfJoinChallengeUseCase(participationRepository, challengeRepository, userRepository);
    }
}