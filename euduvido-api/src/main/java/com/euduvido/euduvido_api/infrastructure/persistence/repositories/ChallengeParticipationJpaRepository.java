package com.euduvido.euduvido_api.infrastructure.persistence.repositories;

import com.euduvido.euduvido_api.infrastructure.persistence.entities.ChallengeParticipationEntity;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeParticipationJpaRepository extends JpaRepository<ChallengeParticipationEntity, Long> {
    List<ChallengeParticipationEntity> findByUserId(Long userId);
    List<ChallengeParticipationEntity> findByChallengeId(Long challengeId);
    Optional<ChallengeParticipationEntity> findByUserIdAndChallengeId(Long userId, Long challengeId);
    List<ChallengeParticipationEntity> findByStatus(ParticipationStatus status);
    Page<ChallengeParticipationEntity> findByUserId(Long userId, Pageable pageable);
    Page<ChallengeParticipationEntity> findByUserIdAndStatus(Long userId, ParticipationStatus status, Pageable pageable);
    Page<ChallengeParticipationEntity> findByChallenge_CreatorIdAndStatus(Long creatorId, ParticipationStatus status, Pageable pageable);
}

