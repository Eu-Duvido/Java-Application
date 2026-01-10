package com.euduvido.euduvido_api.domain.entities;

import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;

import java.time.LocalDateTime;

/**
 * Entidade de domínio que representa a participação de um usuário em um desafio.
 * Rastreia o status da participação (convidado, aceito, recusado, completado).
 */
public class ChallengeParticipation {
    private Long id;
    private User user;
    private Challenge challenge;
    private ParticipationStatus status;
    private LocalDateTime createdAt;

    private ChallengeParticipation(Long id, User user, Challenge challenge, ParticipationStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.challenge = challenge;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Factory method para criar uma nova participação (convidado)
    public static ChallengeParticipation create(User user, Challenge challenge) {
        validateParticipationData(user, challenge);
        return new ChallengeParticipation(null, user, challenge, ParticipationStatus.INVITED, LocalDateTime.now());
    }

    // Factory method para recriar participação do banco de dados
    public static ChallengeParticipation createFromDatabase(Long id, User user, Challenge challenge,
                                                            ParticipationStatus status, LocalDateTime createdAt) {
        return new ChallengeParticipation(id, user, challenge, status, createdAt);
    }

    // Validações de domínio
    private static void validateParticipationData(User user, Challenge challenge) {
        if (user == null) {
            throw new IllegalArgumentException("Participação deve ter um usuário");
        }
        if (challenge == null) {
            throw new IllegalArgumentException("Participação deve estar associada a um desafio");
        }
    }

    // Lógica de negócio: aceitar desafio
    public void accept() {
        if (this.status != ParticipationStatus.INVITED) {
            throw new IllegalStateException("Apenas convites podem ser aceitos");
        }
        this.status = ParticipationStatus.ACCEPTED;
    }

    // Lógica de negócio: recusar desafio
    public void refuse() {
        if (this.status != ParticipationStatus.INVITED) {
            throw new IllegalStateException("Apenas convites podem ser recusados");
        }
        this.status = ParticipationStatus.REFUSED;
    }

    // Lógica de negócio: completar desafio
    public void complete() {
        if (this.status != ParticipationStatus.ACCEPTED) {
            throw new IllegalStateException("Apenas desafios aceitos podem ser completados");
        }
        this.status = ParticipationStatus.COMPLETED;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public ParticipationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

