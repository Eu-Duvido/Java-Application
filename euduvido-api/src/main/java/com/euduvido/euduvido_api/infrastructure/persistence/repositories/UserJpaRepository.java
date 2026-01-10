package com.euduvido.euduvido_api.infrastructure.persistence.repositories;

import com.euduvido.euduvido_api.infrastructure.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório Spring Data JPA para UserEntity.
 * Fornece operações básicas de CRUD para usuários.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    /**
     * Encontrar usuário pelo email
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Verificar se existe usuário com o email
     */
    boolean existsByEmail(String email);
}

