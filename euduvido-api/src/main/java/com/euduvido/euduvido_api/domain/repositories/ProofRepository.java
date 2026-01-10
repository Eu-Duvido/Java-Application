package com.euduvido.euduvido_api.domain.repositories;

import com.euduvido.euduvido_api.domain.entities.Proof;

import java.util.List;
import java.util.Optional;

/**
 * Contrato (interface) de repositório para a entidade Proof.
 * Define as operações que podem ser realizadas com comprovações.
 */
public interface ProofRepository {
    /**
     * Salvar uma nova comprovação ou atualizar uma existente
     */
    Proof save(Proof proof);

    /**
     * Encontrar comprovação pelo ID
     */
    Optional<Proof> findById(Long id);

    /**
     * Listar comprovações de uma participação
     */
    List<Proof> findByParticipationId(Long participationId);

    /**
     * Encontrar comprovação não aprovada de uma participação (última submetida)
     */
    Optional<Proof> findPendingProofByParticipationId(Long participationId);

    /**
     * Deletar uma comprovação pelo ID
     */
    void deleteById(Long id);
}

