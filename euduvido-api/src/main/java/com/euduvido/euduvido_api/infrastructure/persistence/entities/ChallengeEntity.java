package com.euduvido.euduvido_api.infrastructure.persistence.entities;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.domain.enums.Difficulty;
import com.euduvido.euduvido_api.domain.enums.GoalType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", length = 10)
    private Difficulty difficulty;

    @Column(name = "subject", length = 100)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", length = 20)
    private GoalType goalType;

    @Column(name = "goal_value")
    private Integer goalValue;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "challenge_participants",
            joinColumns = @JoinColumn(name = "challenge_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<UserEntity> participants = new ArrayList<>();

    public Challenge toDomain() {
        List<User> domainParticipants = this.participants == null ? new ArrayList<>()
                : this.participants.stream().map(UserEntity::toDomain).collect(Collectors.toList());
        return Challenge.createFromDatabase(
                id, title, description, difficulty, subject, goalType, goalValue,
                creator.toDomain(), deadline, status, locationRequired, createdAt, domainParticipants);
    }

    public static ChallengeEntity fromDomain(Challenge challenge) {
        ChallengeEntity entity = new ChallengeEntity();
        entity.setId(challenge.getId());
        entity.setTitle(challenge.getTitle());
        entity.setDescription(challenge.getDescription());
        entity.setDifficulty(challenge.getDifficulty());
        entity.setSubject(challenge.getSubject());
        entity.setGoalType(challenge.getGoalType());
        entity.setGoalValue(challenge.getGoalValue());
        entity.setCreator(UserEntity.fromDomain(challenge.getCreator()));
        entity.setDeadline(challenge.getDeadline());
        entity.setStatus(challenge.getStatus());
        entity.setLocationRequired(challenge.getLocationRequired());
        entity.setCreatedAt(challenge.getCreatedAt());
        if (challenge.getParticipants() != null) {
            entity.setParticipants(challenge.getParticipants().stream()
                    .map(UserEntity::fromDomain).collect(Collectors.toList()));
        }
        return entity;
    }
}
