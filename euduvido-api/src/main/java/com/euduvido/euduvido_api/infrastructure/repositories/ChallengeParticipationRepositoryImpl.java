package com.euduvido.euduvido_api.infrastructure.repositories;

import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;
import com.euduvido.euduvido_api.infrastructure.persistence.entities.ChallengeParticipationEntity;
import com.euduvido.euduvido_api.infrastructure.persistence.repositories.ChallengeParticipationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementação do repositório de domínio ChallengeParticipationRepository.
 * Adapta o Spring Data JPA para o contrato do domínio.
 */
@Component
public class ChallengeParticipationRepositoryImpl implements ChallengeParticipationRepository {
    private final ChallengeParticipationJpaRepository jpaRepository;

    public ChallengeParticipationRepositoryImpl(ChallengeParticipationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ChallengeParticipation save(ChallengeParticipation participation) {
        ChallengeParticipationEntity entity = ChallengeParticipationEntity.fromDomain(participation);
        ChallengeParticipationEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<ChallengeParticipation> findById(Long id) {
        return jpaRepository.findById(id).map(ChallengeParticipationEntity::toDomain);
    }

    @Override
    public List<ChallengeParticipation> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId)
                .stream()
                .map(ChallengeParticipationEntity::toDomain)
                .toList();
    }

    @Override
    public List<ChallengeParticipation> findByChallengeId(Long challengeId) {
        return jpaRepository.findByChallengeId(challengeId)
                .stream()
                .map(ChallengeParticipationEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<ChallengeParticipation> findByUserIdAndChallengeId(Long userId, Long challengeId) {
        return jpaRepository.findByUserIdAndChallengeId(userId, challengeId)
                .map(ChallengeParticipationEntity::toDomain);
    }

    @Override
    public List<ChallengeParticipation> findByStatus(ParticipationStatus status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(ChallengeParticipationEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}

