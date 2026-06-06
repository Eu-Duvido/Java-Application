package com.euduvido.euduvido_api.infrastructure.persistence.repositories;

import com.euduvido.euduvido_api.infrastructure.persistence.entities.ChallengeEntity;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface ChallengeJpaRepository extends JpaRepository<ChallengeEntity, Long> {
    List<ChallengeEntity> findByCreatorId(Long creatorId);
    List<ChallengeEntity> findByStatus(ChallengeStatus status);
    Page<ChallengeEntity> findByStatus(ChallengeStatus status, Pageable pageable);
    Page<ChallengeEntity> findAll(Pageable pageable);
    List<ChallengeEntity> findByDeadlineBeforeAndStatusIn(LocalDateTime deadline, Collection<ChallengeStatus> statuses);
    Page<ChallengeEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<ChallengeEntity> findByStatusAndTitleContainingIgnoreCase(ChallengeStatus status, String title, Pageable pageable);
}

