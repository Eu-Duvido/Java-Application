package com.euduvido.euduvido_api.domain.repositories;

import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.pagination.PageResult;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteById(Long id);
    List<User> findAll();

    /** Lista paginada de usuários. Ordena por name asc. */
    PageResult<User> findAllPaged(int page, int size);
}
