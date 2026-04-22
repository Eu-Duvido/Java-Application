package com.euduvido.euduvido_api.domain.repositories;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.domain.pagination.PageResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChallengeRepository {
    Challenge save(Challenge challenge);
    Optional<Challenge> findById(Long id);
    List<Challenge> findByCreatorId(Long userId);
    List<Challenge> findByStatus(ChallengeStatus status);
    void deleteById(Long id);
    List<Challenge> findAll();

    /** Lista paginada com filtro opcional de status. Ordena por createdAt desc. */
    PageResult<Challenge> findAllPaged(Optional<ChallengeStatus> status, int page, int size);

    /** Retorna desafios cujo deadline já passou e ainda estão em um dos status fornecidos. */
    List<Challenge> findExpiredCandidates(LocalDateTime before, List<ChallengeStatus> statuses);
}
