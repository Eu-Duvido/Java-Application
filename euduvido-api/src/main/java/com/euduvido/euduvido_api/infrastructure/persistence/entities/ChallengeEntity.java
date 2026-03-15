package com.euduvido.euduvido_api.infrastructure.persistence.entities;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Column(name = "difficulty", nullable = false, columnDefinition = "TEXT")
    private String difficulty;

    @Column(name = "progress", nullable = false, columnDefinition = "TEXT")
    private Double progress;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeStatus status;

    @Column(name = "location_required")
    private Boolean locationRequired;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private UserEntity creator;

    // Changed participants to be a ManyToMany relation to UserEntity instead of a basic column
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "challenge_participants",
            joinColumns = @JoinColumn(name = "challenge_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<UserEntity> participants = new ArrayList<>();

    /**
     * Converte entidade JPA para entidade de domínio
     */
    public Challenge toDomain() {
        List<User> domainParticipants = this.participants == null ? new ArrayList<>() :
                this.participants.stream().map(UserEntity::toDomain).collect(Collectors.toList());

        return Challenge.createFromDatabase(
                id,
                title,
                description,
                difficulty,
                progress,
                creator.toDomain(),
                deadline,
                status,
                locationRequired,
                createdAt,
                domainParticipants
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
        entity.setDifficulty(challenge.getDifficulty());
        entity.setProgress(challenge.getProgress());
        entity.setCreator(UserEntity.fromDomain(challenge.getCreator()));
        entity.setDeadline(challenge.getDeadline());
        entity.setStatus(challenge.getStatus());
        entity.setLocationRequired(challenge.getLocationRequired());
        entity.setCreatedAt(challenge.getCreatedAt());

        if (challenge.getParticipants() != null) {
            List<UserEntity> participantEntities = challenge.getParticipants().stream()
                    .map(UserEntity::fromDomain)
                    .collect(Collectors.toList());
            entity.setParticipants(participantEntities);
        }

        return entity;
    }
}
