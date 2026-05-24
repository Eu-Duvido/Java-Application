package com.euduvido.euduvido_api.application.usecases.user;

import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.pagination.PageResult;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;

public class ListUserUseCase {
    private final UserRepository userRepository;

    public ListUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PageResult<User> execute(int page, int size) {
        return userRepository.findAllPaged(page, size);
    }
}