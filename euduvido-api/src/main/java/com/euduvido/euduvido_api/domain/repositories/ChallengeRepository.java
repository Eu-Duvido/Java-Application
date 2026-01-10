package com.euduvido.euduvido_api.domain.repositories;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;

import java.util.List;
import java.util.Optional;

/**
 * Contrato (interface) de repositório para a entidade Challenge.
 * Define as operações que podem ser realizadas com desafios.
 */
public interface ChallengeRepository {
    /**
     * Salvar um novo desafio ou atualizar um existente
     */
    Challenge save(Challenge challenge);

    /**
     * Encontrar desafio pelo ID
     */
    Optional<Challenge> findById(Long id);

    /**
     * Listar todos os desafios criados por um usuário
     */
    List<Challenge> findByCreatorId(Long userId);

    /**
     * Listar desafios por status
     */
    List<Challenge> findByStatus(ChallengeStatus status);

    /**
     * Deletar um desafio pelo ID
     */
    void deleteById(Long id);
}

