package com.euduvido.euduvido_api.domain.entities;

import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.domain.enums.Difficulty;
import com.euduvido.euduvido_api.domain.enums.GoalType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Challenge {
    private Long id;
    private String title;
    private String description;
    private Difficulty difficulty;
    private String subject;
    private GoalType goalType;
    private Integer goalValue;
    private ChallengeStatus status;
    private LocalDateTime deadline;
    private Boolean locationRequired;
    private LocalDateTime createdAt;
    private User creator;
    private List<User> participants;

    public Challenge() {}

    private Challenge(Long id, String title, String description, Difficulty difficulty, String subject,
                      GoalType goalType, Integer goalValue, ChallengeStatus status, LocalDateTime deadline,
                      Boolean locationRequired, LocalDateTime createdAt, User creator, List<User> participants) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.subject = subject;
        this.goalType = goalType;
        this.goalValue = goalValue;
        this.status = status;
        this.deadline = deadline;
        this.locationRequired = locationRequired;
        this.createdAt = createdAt;
        this.creator = creator;
        this.participants = participants;
    }

    public static Challenge create(String title, String description, User creator, LocalDateTime deadline,
                                   Boolean locationRequired, Difficulty difficulty, String subject,
                                   GoalType goalType, Integer goalValue) {
        validateChallengeData(title, description, creator, deadline, goalType, goalValue);
        return new Challenge(null, title, description, difficulty, subject, goalType, goalValue,
                ChallengeStatus.PENDING, deadline, locationRequired, LocalDateTime.now(), creator, new ArrayList<>());
    }

    public static Challenge createFromDatabase(Long id, String title, String description, Difficulty difficulty,
                                               String subject, GoalType goalType, Integer goalValue,
                                               User creator, LocalDateTime deadline, ChallengeStatus status,
                                               Boolean locationRequired, LocalDateTime createdAt,
                                               List<User> participants) {
        return new Challenge(id, title, description, difficulty, subject, goalType, goalValue, status, deadline,
                locationRequired, createdAt, creator, participants == null ? new ArrayList<>() : participants);
    }

    private static void validateChallengeData(String title, String description, User creator,
                                               LocalDateTime deadline, GoalType goalType, Integer goalValue) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Título do desafio não pode ser vazio");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição do desafio não pode ser vazia");
        }
        if (creator == null) {
            throw new IllegalArgumentException("Desafio deve ter um criador");
        }
        if (deadline == null || deadline.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Deadline deve ser no futuro");
        }
        if (goalType != null && (goalValue == null || goalValue < 1)) {
            throw new IllegalArgumentException("goalValue deve ser maior que zero quando goalType é informado");
        }
    }

    public void activate() {
        if (this.status != ChallengeStatus.PENDING) {
            throw new IllegalStateException("Apenas desafios pendentes podem ser ativados");
        }
        this.status = ChallengeStatus.ACTIVE;
    }

    public void complete() {
        if (this.status != ChallengeStatus.ACTIVE) {
            throw new IllegalStateException("Apenas desafios ativos podem ser completados");
        }
        this.status = ChallengeStatus.COMPLETED;
    }

    public void expire() {
        if (this.status == ChallengeStatus.COMPLETED) {
            throw new IllegalStateException("Desafios completados não podem expirar");
        }
        this.status = ChallengeStatus.EXPIRED;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.deadline);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public GoalType getGoalType() { return goalType; }
    public void setGoalType(GoalType goalType) { this.goalType = goalType; }
    public Integer getGoalValue() { return goalValue; }
    public void setGoalValue(Integer goalValue) { this.goalValue = goalValue; }
    public ChallengeStatus getStatus() { return status; }
    public void setStatus(ChallengeStatus status) { this.status = status; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public Boolean getLocationRequired() { return locationRequired; }
    public void setLocationRequired(Boolean locationRequired) { this.locationRequired = locationRequired; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }
    public List<User> getParticipants() { return participants; }
    public void setParticipants(List<User> participants) { this.participants = participants; }
}
