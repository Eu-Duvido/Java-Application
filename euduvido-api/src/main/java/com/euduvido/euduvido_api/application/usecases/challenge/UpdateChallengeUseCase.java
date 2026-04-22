package com.euduvido.euduvido_api.application.usecases.challenge;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.enums.Difficulty;
import com.euduvido.euduvido_api.domain.enums.GoalType;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;

import java.time.LocalDateTime;

public class UpdateChallengeUseCase {
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    public UpdateChallengeUseCase(ChallengeRepository challengeRepository, UserRepository userRepository) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
    }

    /** Atualiza os campos fornecidos; mantém valores existentes quando o campo é null. */
    public Challenge execute(Long challengeId, Long creatorId, String title, String description,
                             LocalDateTime deadline, Boolean locationRequired, Difficulty difficulty,
                             String subject, GoalType goalType, Integer goalValue) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário criador não encontrado"));
        Challenge existing = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Desafio não encontrado"));

        Challenge updated = Challenge.createFromDatabase(
                existing.getId(),
                title        != null ? title        : existing.getTitle(),
                description  != null ? description  : existing.getDescription(),
                difficulty   != null ? difficulty   : existing.getDifficulty(),
                subject      != null ? subject      : existing.getSubject(),
                goalType     != null ? goalType     : existing.getGoalType(),
                goalValue    != null ? goalValue    : existing.getGoalValue(),
                creator,
                deadline     != null ? deadline     : existing.getDeadline(),
                existing.getStatus(),
                locationRequired != null ? locationRequired : existing.getLocationRequired(),
                existing.getCreatedAt(),
                existing.getParticipants());
        return challengeRepository.save(updated);
    }
}
