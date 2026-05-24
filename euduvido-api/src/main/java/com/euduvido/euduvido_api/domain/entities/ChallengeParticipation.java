package com.euduvido.euduvido_api.domain.entities;

import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;

public class ChallengeParticipation {
    private Long id;
    private User user;
    private Challenge challenge;
    private ParticipationStatus status;
    private Integer progress;

    public ChallengeParticipation() {}

    private ChallengeParticipation(Long id, User user, Challenge challenge,
                                    ParticipationStatus status, Integer progress) {
        this.id = id;
        this.user = user;
        this.challenge = challenge;
        this.status = status;
        this.progress = progress;
    }

    public static ChallengeParticipation create(User user, Challenge challenge) {
        if (user == null) throw new IllegalArgumentException("Participação deve ter um usuário");
        if (challenge == null) throw new IllegalArgumentException("Participação deve estar associada a um desafio");
        return new ChallengeParticipation(null, user, challenge, ParticipationStatus.INVITED, 0);
    }

    public static ChallengeParticipation createFromDatabase(Long id, User user, Challenge challenge,
                                                             ParticipationStatus status, Integer progress) {
        return new ChallengeParticipation(id, user, challenge, status, progress == null ? 0 : progress);
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

    public void updateProgress(Integer newProgress) {
        if (this.status != ParticipationStatus.ACCEPTED) {
            throw new IllegalStateException("Só é possível atualizar progresso em participações aceitas");
        }
        if (newProgress == null || newProgress < 0) {
            throw new IllegalArgumentException("Progresso não pode ser negativo");
        }
        Integer goalValue = this.challenge.getGoalValue();
        if (goalValue != null && newProgress > goalValue) {
            throw new IllegalArgumentException(
                    "Progresso não pode ser maior que a meta do desafio (" + goalValue + ")");
        }
        this.progress = newProgress;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Challenge getChallenge() { return challenge; }
    public void setChallenge(Challenge challenge) { this.challenge = challenge; }
    public ParticipationStatus getStatus() { return status; }
    public void setStatus(ParticipationStatus status) { this.status = status; }
    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }
}
