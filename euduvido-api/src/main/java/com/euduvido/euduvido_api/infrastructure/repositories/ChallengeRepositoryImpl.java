package com.euduvido.euduvido_api.infrastructure.repositories;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;
import com.euduvido.euduvido_api.infrastructure.persistence.entities.ChallengeEntity;
import com.euduvido.euduvido_api.infrastructure.persistence.repositories.ChallengeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementação do repositório de domínio ChallengeRepository.
 * Adapta o Spring Data JPA para o contrato do domínio.
 */
@Component
public class ChallengeRepositoryImpl implements ChallengeRepository {
    private final ChallengeJpaRepository jpaRepository;

    public ChallengeRepositoryImpl(ChallengeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Challenge save(Challenge challenge) {
        ChallengeEntity entity = ChallengeEntity.fromDomain(challenge);
        ChallengeEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Challenge> findById(Long id) {
        return jpaRepository.findById(id).map(ChallengeEntity::toDomain);
    }

    @Override
    public List<Challenge> findByCreatorId(Long userId) {
        return jpaRepository.findByCreatorId(userId)
                .stream()
                .map(ChallengeEntity::toDomain)
                .toList();
    }

    @Override
    public List<Challenge> findByStatus(ChallengeStatus status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(ChallengeEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}

