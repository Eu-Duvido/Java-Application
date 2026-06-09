package com.euduvido.euduvido_api.infrastructure.persistence.repositories;

import com.euduvido.euduvido_api.infrastructure.persistence.entities.AiInsightEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiInsightJpaRepository extends JpaRepository<AiInsightEntity, Long> {

    List<AiInsightEntity> findByNoCursoAndNoRegiaoOrderByDtGeracaoDesc(
        String noCurso,
        String noRegiao
    );

    List<AiInsightEntity> findTop10ByOrderByDtGeracaoDesc();

    /**
     * Verifica se já existe insight para o perfil.
     * Evita chamar o Gemini toda vez que o usuário abrir o app.
     */
    boolean existsByNoCursoAndNoRegiaoAndTpModalidade(
        String noCurso,
        String noRegiao,
        Integer tpModalidade
    );
}
