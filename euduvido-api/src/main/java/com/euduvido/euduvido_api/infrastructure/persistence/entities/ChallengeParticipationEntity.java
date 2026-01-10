package com.euduvido.euduvido_api.infrastructure.persistence.entities;

import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade JPA que mapeia a tabela de participações em desafios.
 * Representa o mapeamento técnico da entidade de domínio ChallengeParticipation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "challenge_participations")
public class ChallengeParticipationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private ChallengeEntity challenge;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Converte entidade JPA para entidade de domínio
     */
    public ChallengeParticipation toDomain() {
        return ChallengeParticipation.createFromDatabase(
                id,
                user.toDomain(),
                challenge.toDomain(),
                status,
                createdAt
        );
    }

    /**
     * Cria entidade JPA a partir de entidade de domínio
     */
    public static ChallengeParticipationEntity fromDomain(ChallengeParticipation participation) {
        ChallengeParticipationEntity entity = new ChallengeParticipationEntity();
        entity.setId(participation.getId());
        entity.setUser(UserEntity.fromDomain(participation.getUser()));
        entity.setChallenge(ChallengeEntity.fromDomain(participation.getChallenge()));
        entity.setStatus(participation.getStatus());
        entity.setCreatedAt(participation.getCreatedAt());
        return entity;
    }
}

