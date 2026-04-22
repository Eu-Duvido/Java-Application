package com.euduvido.euduvido_api.application.usecases.challenge;

import com.euduvido.euduvido_api.application.exception.AiValidationException;
import com.euduvido.euduvido_api.application.services.AiValidationService;
import com.euduvido.euduvido_api.application.services.ValidationResult;
import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.enums.Difficulty;
import com.euduvido.euduvido_api.domain.enums.GoalType;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;

import java.time.LocalDateTime;

public class CreateChallengeUseCase {
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final AiValidationService aiValidationService;

    public CreateChallengeUseCase(ChallengeRepository challengeRepository, UserRepository userRepository,
                                   AiValidationService aiValidationService) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
        this.aiValidationService = aiValidationService;
    }

    /** Cria um novo desafio acadêmico com validação por IA. */
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
        return challengeRepository.save(challenge);
    }
}
