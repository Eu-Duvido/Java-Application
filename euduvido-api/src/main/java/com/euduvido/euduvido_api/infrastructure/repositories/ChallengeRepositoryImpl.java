package com.euduvido.euduvido_api.infrastructure.repositories;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.domain.pagination.PageResult;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;
import com.euduvido.euduvido_api.infrastructure.persistence.entities.ChallengeEntity;
import com.euduvido.euduvido_api.infrastructure.persistence.repositories.ChallengeJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ChallengeRepositoryImpl implements ChallengeRepository {
    private final ChallengeJpaRepository jpaRepository;

    public ChallengeRepositoryImpl(ChallengeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Challenge save(Challenge challenge) {
        return jpaRepository.save(ChallengeEntity.fromDomain(challenge)).toDomain();
    }

    @Override
    public Optional<Challenge> findById(Long id) {
        return jpaRepository.findById(id).map(ChallengeEntity::toDomain);
    }

    @Override
    public List<Challenge> findByCreatorId(Long userId) {
        return jpaRepository.findByCreatorId(userId).stream().map(ChallengeEntity::toDomain).toList();
    }

    @Override
    public List<Challenge> findByStatus(ChallengeStatus status) {
        return jpaRepository.findByStatus(status).stream().map(ChallengeEntity::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Challenge> findAll() {
        return jpaRepository.findAll().stream().map(ChallengeEntity::toDomain).toList();
    }

    @Override
    public List<Challenge> findExpiredCandidates(LocalDateTime before, List<ChallengeStatus> statuses) {
        return jpaRepository.findByDeadlineBeforeAndStatusIn(before, statuses)
                .stream().map(ChallengeEntity::toDomain).toList();
    }

    @Override
    public PageResult<Challenge> findAllPaged(Optional<ChallengeStatus> status, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ChallengeEntity> result = status.isPresent()
                ? jpaRepository.findByStatus(status.get(), pageable)
                : jpaRepository.findAll(pageable);
        return new PageResult<>(
                result.getContent().stream().map(ChallengeEntity::toDomain).toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }
}

