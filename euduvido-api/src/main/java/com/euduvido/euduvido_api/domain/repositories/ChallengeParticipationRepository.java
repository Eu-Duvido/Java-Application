package com.euduvido.euduvido_api.domain.repositories;

import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import com.euduvido.euduvido_api.domain.pagination.PageResult;

import java.util.List;
import java.util.Optional;

public interface ChallengeParticipationRepository {
    ChallengeParticipation save(ChallengeParticipation participation);
    Optional<ChallengeParticipation> findById(Long id);
    List<ChallengeParticipation> findByUserId(Long userId);
    List<ChallengeParticipation> findByChallengeId(Long challengeId);
    Optional<ChallengeParticipation> findByUserIdAndChallengeId(Long userId, Long challengeId);
    List<ChallengeParticipation> findByStatus(ParticipationStatus status);
    void deleteById(Long id);

    /** Lista paginada de participações de um usuário com filtro opcional de status. */
    PageResult<ChallengeParticipation> findByUserIdPaged(Long userId,
                                                          Optional<ParticipationStatus> status,
                                                          int page, int size);

    /** Lista paginada de participações em desafios criados pelo usuário, com filtro obrigatório de status. */
    PageResult<ChallengeParticipation> findByChallengeCreatorIdAndStatusPaged(Long creatorId,
                                                                               ParticipationStatus status,
                                                                               int page, int size);
}
