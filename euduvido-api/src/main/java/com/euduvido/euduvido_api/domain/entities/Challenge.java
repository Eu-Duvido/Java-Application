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

    // Factory method para criar um novo desafio
    public static Challenge create(String title, String description, User creator, LocalDateTime deadline, Boolean locationRequired) {
        validateChallengeData(title, description, creator, deadline);
        return new Challenge(null, title, description, creator, deadline, ChallengeStatus.PENDING, locationRequired, LocalDateTime.now());
    }

    // Factory method para recriar desafio do banco de dados
    public static Challenge createFromDatabase(Long id, String title, String description, User creator,
                                               LocalDateTime deadline, ChallengeStatus status,
                                               Boolean locationRequired, LocalDateTime createdAt) {
        return new Challenge(id, title, description, creator, deadline, status, locationRequired, createdAt);
    }

    // Validações de domínio
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

    // Lógica de negócio: ativar desafio
    public void activate() {
        if (this.status != ChallengeStatus.PENDING) {
            throw new IllegalStateException("Apenas desafios pendentes podem ser ativados");
        }
        this.status = ChallengeStatus.ACTIVE;
    }

    // Lógica de negócio: completar desafio
    public void complete() {
        if (this.status != ChallengeStatus.ACTIVE) {
            throw new IllegalStateException("Apenas desafios ativos podem ser completados");
        }
        this.status = ChallengeStatus.COMPLETED;
    }

    // Lógica de negócio: expirar desafio
    public void expire() {
        if (this.status == ChallengeStatus.COMPLETED) {
            throw new IllegalStateException("Desafios completados não podem expirar");
        }
        this.status = ChallengeStatus.EXPIRED;
    }

    // Verifica se o desafio expirou
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.deadline);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public User getCreator() {
        return creator;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public ChallengeStatus getStatus() {
        return status;
    }

    public Boolean getLocationRequired() {
        return locationRequired;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

