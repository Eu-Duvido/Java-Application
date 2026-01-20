package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;

import java.util.List;

/**
 * Caso de uso: Listar todos os usuários.
 * Responsabilidade: Listar todos os usuários presente no banco de dados.
 */

public class ListUserUseCase {
    private final UserRepository userRepository;

    public ListUserUseCase(UserRepository userRepository) { this.userRepository = userRepository; }

    /**
     * Executa a listagem dos usuários
     */

    public List<User> execute() {
        return userRepository.findAll();
    }
}
