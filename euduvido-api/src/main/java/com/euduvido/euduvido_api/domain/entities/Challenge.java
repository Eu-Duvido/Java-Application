package com.euduvido.euduvido_api.domain.entities;

import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * Entidade de domínio que representa um desafio no sistema.
 * Um desafio é criado por um usuário e pode ser enviado para outros usuários.
 */
public class Challenge {
    private Long id;
    private String title;
    private String description;
    private String difficulty;
    private Double progress;
    private ChallengeStatus status;
    private LocalDateTime deadline;
    private Boolean locationRequired;
    private LocalDateTime createdAt;
    private User creator;
    private List<User> participants;

    public Challenge() {
    }

    public Challenge(Long id, String title, String description, String difficulty, Double progress, ChallengeStatus status, LocalDateTime deadline, Boolean locationRequired, LocalDateTime createdAt, User creator, List<User> participants) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.progress = progress;
        this.status = status;
        this.deadline = deadline;
        this.locationRequired = locationRequired;
        this.createdAt = createdAt;
        this.creator = creator;
        this.participants = participants;
    }

    public static Challenge create(String title, String subtitle, User creator, LocalDateTime deadline, Boolean locationRequired) {
        validateChallengeData(title, subtitle, creator, deadline);
        // Preencher campos opcionais com valores padrão
        String defaultDifficulty = null;
        Double defaultProgress = 0.0;
        List<User> defaultParticipants = new ArrayList<>();
        return new Challenge(
                null,
                title,
                subtitle,
                defaultDifficulty,
                defaultProgress,
                ChallengeStatus.PENDING,
                deadline,
                locationRequired,
                LocalDateTime.now(),
                creator,
                defaultParticipants
        );
    }

    public static Challenge createFromDatabase(Long id, String title, String subtitle, String difficulty, Double progress, User creator,
                                               LocalDateTime deadline, ChallengeStatus status,
                                               Boolean locationRequired, LocalDateTime createdAt, List<User> participants) {
        List<User> safeParticipants = participants == null ? new ArrayList<>() : participants;
        return new Challenge(id, title, subtitle, difficulty, progress, status, deadline, locationRequired, createdAt, creator, safeParticipants);
    }

    private static void validateChallengeData(String title, String subtitle, User creator, LocalDateTime deadline) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Título do desafio não pode ser vazio");
        }
        if (subtitle == null || subtitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição do desafio não pode ser vazia");
        }
        if (creator == null) {
            throw new IllegalArgumentException("Desafio deve ter um criador");
        }
        if (deadline == null || deadline.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Deadline deve ser no futuro");
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public ChallengeStatus getStatus() {
        return status;
    }

    public void setStatus(ChallengeStatus status) {
        this.status = status;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Boolean getLocationRequired() {
        return locationRequired;
    }

    public void setLocationRequired(Boolean locationRequired) {
        this.locationRequired = locationRequired;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }
}
