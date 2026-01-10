package com.euduvido.euduvido_api.infrastructure.persistence.repositories;

import com.euduvido.euduvido_api.infrastructure.persistence.entities.ProofEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório Spring Data JPA para ProofEntity.
 * Fornece operações de persistência para comprovações.
 */
@Repository
public interface ProofJpaRepository extends JpaRepository<ProofEntity, Long> {
    /**
     * Encontrar comprovações de uma participação
     */
    List<ProofEntity> findByParticipationId(Long participationId);

    /**
     * Encontrar comprovação não aprovada de uma participação
     */
    @Query("SELECT p FROM ProofEntity p WHERE p.participation.id = :participationId AND p.approved = false ORDER BY p.submittedAt DESC LIMIT 1")
    Optional<ProofEntity> findPendingProofByParticipationId(Long participationId);
}

