package com.euduvido.euduvido_api.domain.entities;

import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;

import java.time.LocalDateTime;

/**
 * Entidade de domínio que representa um desafio no sistema.
 * Um desafio é criado por um usuário e pode ser enviado para outros usuários.
 */
public class Challenge {
    private Long id;
    private String title;
    private String description;
    private User creator;
    private LocalDateTime deadline;
    private ChallengeStatus status;
    private Boolean locationRequired;
    private LocalDateTime createdAt;

    public Challenge() {
    }

    private Challenge(Long id, String title, String description, User creator, LocalDateTime deadline,
                      ChallengeStatus status, Boolean locationRequired, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.deadline = deadline;
        this.status = status;
        this.locationRequired = locationRequired;
        this.createdAt = createdAt;
    }

    public static Challenge create(String title, String description, User creator, LocalDateTime deadline, Boolean locationRequired) {
        validateChallengeData(title, description, creator, deadline);
        return new Challenge(null, title, description, creator, deadline, ChallengeStatus.PENDING, locationRequired, LocalDateTime.now());
    }

    public static Challenge createFromDatabase(Long id, String title, String description, User creator,
                                               LocalDateTime deadline, ChallengeStatus status,
                                               Boolean locationRequired, LocalDateTime createdAt) {
        return new Challenge(id, title, description, creator, deadline, status, locationRequired, createdAt);
    }

    private static void validateChallengeData(String title, String description, User creator, LocalDateTime deadline) {
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

    public User getCreator() {
        return creator;
    }
    public void setCreator(User creator) {
        this.creator = creator;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public ChallengeStatus getStatus() {
        return status;
    }
    public void setStatus(ChallengeStatus status) {
        this.status = status;
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
}

