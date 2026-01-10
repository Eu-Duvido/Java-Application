package com.euduvido.euduvido_api.infrastructure.persistence.repositories;

import com.euduvido.euduvido_api.infrastructure.persistence.entities.ChallengeEntity;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório Spring Data JPA para ChallengeEntity.
 * Fornece operações de persistência para desafios.
 */
@Repository
public interface ChallengeJpaRepository extends JpaRepository<ChallengeEntity, Long> {
    /**
     * Encontrar desafios criados por um usuário
     */
    List<ChallengeEntity> findByCreatorId(Long creatorId);

    /**
     * Encontrar desafios por status
     */
    List<ChallengeEntity> findByStatus(ChallengeStatus status);
}

