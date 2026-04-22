package com.euduvido.euduvido_api.infrastructure.repositories;

import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import com.euduvido.euduvido_api.domain.pagination.PageResult;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;
import com.euduvido.euduvido_api.infrastructure.persistence.entities.ChallengeParticipationEntity;
import com.euduvido.euduvido_api.infrastructure.persistence.repositories.ChallengeParticipationJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ChallengeParticipationRepositoryImpl implements ChallengeParticipationRepository {
    private final ChallengeParticipationJpaRepository jpaRepository;

    public ChallengeParticipationRepositoryImpl(ChallengeParticipationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ChallengeParticipation save(ChallengeParticipation participation) {
        return jpaRepository.save(ChallengeParticipationEntity.fromDomain(participation)).toDomain();
    }

    @Override
    public Optional<ChallengeParticipation> findById(Long id) {
        return jpaRepository.findById(id).map(ChallengeParticipationEntity::toDomain);
    }

    @Override
    public List<ChallengeParticipation> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream().map(ChallengeParticipationEntity::toDomain).toList();
    }

    @Override
    public List<ChallengeParticipation> findByChallengeId(Long challengeId) {
        return jpaRepository.findByChallengeId(challengeId).stream().map(ChallengeParticipationEntity::toDomain).toList();
    }

    @Override
    public Optional<ChallengeParticipation> findByUserIdAndChallengeId(Long userId, Long challengeId) {
        return jpaRepository.findByUserIdAndChallengeId(userId, challengeId).map(ChallengeParticipationEntity::toDomain);
    }

    @Override
    public List<ChallengeParticipation> findByStatus(ParticipationStatus status) {
        return jpaRepository.findByStatus(status).stream().map(ChallengeParticipationEntity::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public PageResult<ChallengeParticipation> findByUserIdPaged(Long userId, Optional<ParticipationStatus> status, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<ChallengeParticipationEntity> result = status.isPresent()
                ? jpaRepository.findByUserIdAndStatus(userId, status.get(), pageable)
                : jpaRepository.findByUserId(userId, pageable);
        return toPageResult(result);
    }

    @Override
    public PageResult<ChallengeParticipation> findByChallengeCreatorIdAndStatusPaged(Long creatorId,
                                                                                      ParticipationStatus status,
                                                                                      int page, int size) {
        Page<ChallengeParticipationEntity> result =
                jpaRepository.findByChallenge_CreatorIdAndStatus(creatorId, status, PageRequest.of(page, size));
        return toPageResult(result);
    }

    private PageResult<ChallengeParticipation> toPageResult(Page<ChallengeParticipationEntity> page) {
        return new PageResult<>(
                page.getContent().stream().map(ChallengeParticipationEntity::toDomain).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }
}

