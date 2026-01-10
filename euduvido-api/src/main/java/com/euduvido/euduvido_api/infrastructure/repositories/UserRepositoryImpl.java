package com.euduvido.euduvido_api.infrastructure.repositories;

import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;
import com.euduvido.euduvido_api.infrastructure.persistence.entities.UserEntity;
import com.euduvido.euduvido_api.infrastructure.persistence.repositories.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementação do repositório de domínio UserRepository.
 * Adapta o Spring Data JPA para o contrato do domínio.
 * A dependência SEMPRE aponta para dentro (domain não depende de infrastructure).
 */
@Component
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpaRepository;

    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.fromDomain(user);
        UserEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(UserEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}

