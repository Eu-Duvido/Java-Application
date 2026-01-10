package com.euduvido.euduvido_api.infrastructure.persistence.entities;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade JPA que mapeia a tabela de desafios no banco de dados.
 * Representa o mapeamento técnico da entidade de domínio Challenge.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "challenges")
public class ChallengeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private UserEntity creator;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeStatus status;

    @Column(name = "location_required")
    private Boolean locationRequired;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Converte entidade JPA para entidade de domínio
     */
    public Challenge toDomain() {
        return Challenge.createFromDatabase(
                id,
                title,
                description,
                creator.toDomain(),
                deadline,
                status,
                locationRequired,
                createdAt
        );
    }

    /**
     * Cria entidade JPA a partir de entidade de domínio
     */
    public static ChallengeEntity fromDomain(Challenge challenge) {
        ChallengeEntity entity = new ChallengeEntity();
        entity.setId(challenge.getId());
        entity.setTitle(challenge.getTitle());
        entity.setDescription(challenge.getDescription());
        entity.setCreator(UserEntity.fromDomain(challenge.getCreator()));
        entity.setDeadline(challenge.getDeadline());
        entity.setStatus(challenge.getStatus());
        entity.setLocationRequired(challenge.getLocationRequired());
        entity.setCreatedAt(challenge.getCreatedAt());
        return entity;
    }
}

