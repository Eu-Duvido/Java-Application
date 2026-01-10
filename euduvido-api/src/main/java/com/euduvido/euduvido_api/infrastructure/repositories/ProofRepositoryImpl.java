package com.euduvido.euduvido_api.infrastructure.repositories;

import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.repositories.ProofRepository;
import com.euduvido.euduvido_api.infrastructure.persistence.entities.ProofEntity;
import com.euduvido.euduvido_api.infrastructure.persistence.repositories.ProofJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementação do repositório de domínio ProofRepository.
 * Adapta o Spring Data JPA para o contrato do domínio.
 */
@Component
public class ProofRepositoryImpl implements ProofRepository {
    private final ProofJpaRepository jpaRepository;

    public ProofRepositoryImpl(ProofJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Proof save(Proof proof) {
        ProofEntity entity = ProofEntity.fromDomain(proof);
        ProofEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Proof> findById(Long id) {
        return jpaRepository.findById(id).map(ProofEntity::toDomain);
    }

    @Override
    public List<Proof> findByParticipationId(Long participationId) {
        return jpaRepository.findByParticipationId(participationId)
                .stream()
                .map(ProofEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Proof> findPendingProofByParticipationId(Long participationId) {
        return jpaRepository.findPendingProofByParticipationId(participationId)
                .map(ProofEntity::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}

