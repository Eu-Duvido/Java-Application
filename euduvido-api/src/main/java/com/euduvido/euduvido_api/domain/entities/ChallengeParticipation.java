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
    private String level;

    public ChallengeParticipation() {
    }

    private ChallengeParticipation(Long id, User user, Challenge challenge, ParticipationStatus status, String level) {
        this.id = id;
        this.user = user;
        this.challenge = challenge;
        this.status = status;
        this.level = level;
    }

    public static ChallengeParticipation create(User user, Challenge challenge) {
        validateParticipationData(user, challenge);
        return new ChallengeParticipation(null, user, challenge, ParticipationStatus.INVITED, null);
    }

    public static ChallengeParticipation createFromDatabase(Long id, User user, Challenge challenge,
                                                            ParticipationStatus status, String level) {
        return new ChallengeParticipation(id, user, challenge, status, level);
    }

    private static void validateParticipationData(User user, Challenge challenge) {
        if (user == null) {
            throw new IllegalArgumentException("Participação deve ter um usuário");
        }
        if (challenge == null) {
            throw new IllegalArgumentException("Participação deve estar associada a um desafio");
        }
    }

    public void accept() {
        if (this.status != ParticipationStatus.INVITED) {
            throw new IllegalStateException("Apenas convites podem ser aceitos");
        }
        this.status = ParticipationStatus.ACCEPTED;
    }

    public void refuse() {
        if (this.status != ParticipationStatus.INVITED) {
            throw new IllegalStateException("Apenas convites podem ser recusados");
        }
        this.status = ParticipationStatus.REFUSED;
    }

    public void complete() {
        if (this.status != ParticipationStatus.ACCEPTED) {
            throw new IllegalStateException("Apenas desafios aceitos podem ser completados");
        }
        this.status = ParticipationStatus.COMPLETED;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Challenge getChallenge() {
        return challenge;
    }
    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public ParticipationStatus getStatus() {
        return status;
    }
    public void setStatus(ParticipationStatus status) {
        this.status = status;
    }

    public String getLevel() {
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
}
}

