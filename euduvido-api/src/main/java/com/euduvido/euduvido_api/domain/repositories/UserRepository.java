package com.euduvido.euduvido_api.domain.repositories;

import com.euduvido.euduvido_api.domain.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * Contrato (interface) de repositório para a entidade User.
 * Define as operações que podem ser realizadas com usuários.
 * A implementação real fica na camada de infrastructure.
 */
public interface UserRepository {
    /**
     * Salvar um novo usuário ou atualizar um existente
     */
    User save(User user);

    /**
     * Encontrar usuário pelo ID
     */
    Optional<User> findById(Long id);

    /**
     * Encontrar usuário pelo email
     */
    Optional<User> findByEmail(String email);

    /**
     * Verificar se um usuário existe por email
     */
    boolean existsByEmail(String email);

    /**
     * Deletar um usuário pelo ID
     */
    void deleteById(Long id);

    /**
     * Listar todos os usuários
     */
    List<User> findAll();
}

