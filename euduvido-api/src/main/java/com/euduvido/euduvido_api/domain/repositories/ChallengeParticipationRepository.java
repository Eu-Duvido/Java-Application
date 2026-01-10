package com.euduvido.euduvido_api.domain.repositories;

import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;

import java.util.List;
import java.util.Optional;

/**
 * Contrato (interface) de repositório para a entidade ChallengeParticipation.
 * Define as operações que podem ser realizadas com participações.
 */
public interface ChallengeParticipationRepository {
    /**
     * Salvar uma nova participação ou atualizar uma existente
     */
    ChallengeParticipation save(ChallengeParticipation participation);

    /**
     * Encontrar participação pelo ID
     */
    Optional<ChallengeParticipation> findById(Long id);

    /**
     * Listar participações de um usuário
     */
    List<ChallengeParticipation> findByUserId(Long userId);

    /**
     * Listar participações de um desafio
     */
    List<ChallengeParticipation> findByChallengeId(Long challengeId);

    /**
     * Encontrar participação específica de um usuário em um desafio
     */
    Optional<ChallengeParticipation> findByUserIdAndChallengeId(Long userId, Long challengeId);

    /**
     * Listar participações por status
     */
    List<ChallengeParticipation> findByStatus(ParticipationStatus status);

    /**
     * Deletar uma participação pelo ID
     */
    void deleteById(Long id);
}

