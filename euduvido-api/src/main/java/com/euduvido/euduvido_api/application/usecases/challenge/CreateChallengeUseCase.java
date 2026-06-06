package com.euduvido.euduvido_api.application.usecases.challenge;

import com.euduvido.euduvido_api.application.exception.AiValidationException;
import com.euduvido.euduvido_api.application.services.AiValidationService;
import com.euduvido.euduvido_api.application.services.ValidationResult;
import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.enums.Difficulty;
import com.euduvido.euduvido_api.domain.enums.GoalType;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;

import java.time.LocalDateTime;

public class CreateChallengeUseCase {
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final ChallengeParticipationRepository participationRepository;
    private final AiValidationService aiValidationService;

    public CreateChallengeUseCase(ChallengeRepository challengeRepository, UserRepository userRepository,
                                   ChallengeParticipationRepository participationRepository,
                                   AiValidationService aiValidationService) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
        this.participationRepository = participationRepository;
        this.aiValidationService = aiValidationService;
    }

    /** Cria um novo desafio e registra uma participação ACCEPTED para o criador automaticamente. */
    public Challenge execute(Long creatorId, String title, String description, LocalDateTime deadline,
                             Boolean locationRequired, Difficulty difficulty, String subject,
                             GoalType goalType, Integer goalValue) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário criador não encontrado"));

        ValidationResult validation = aiValidationService.validateChallenge(title, description, subject);
        if (!validation.valid()) {
            throw new AiValidationException(validation);
        }

        Challenge challenge = Challenge.create(title, description, creator, deadline,
                locationRequired, difficulty, subject, goalType, goalValue);
        Challenge saved = challengeRepository.save(challenge);

        participationRepository.save(ChallengeParticipation.createForCreator(creator, saved));

        return saved;
    }
}
