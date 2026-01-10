package com.euduvido.euduvido_api.infrastructure.persistence.repositories;

import com.euduvido.euduvido_api.infrastructure.persistence.entities.ChallengeParticipationEntity;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório Spring Data JPA para ChallengeParticipationEntity.
 * Fornece operações de persistência para participações.
 */
@Repository
public interface ChallengeParticipationJpaRepository extends JpaRepository<ChallengeParticipationEntity, Long> {
    /**
     * Encontrar participações de um usuário
     */
    List<ChallengeParticipationEntity> findByUserId(Long userId);

    /**
     * Encontrar participações de um desafio
     */
    List<ChallengeParticipationEntity> findByChallengeId(Long challengeId);

    /**
     * Encontrar participação específica de um usuário em um desafio
     */
    Optional<ChallengeParticipationEntity> findByUserIdAndChallengeId(Long userId, Long challengeId);

    /**
     * Encontrar participações por status
     */
    List<ChallengeParticipationEntity> findByStatus(ParticipationStatus status);
}

