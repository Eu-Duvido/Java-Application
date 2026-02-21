package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;

import java.util.Optional;

/**
 * Caso de uso: Deletar um usuário.
 * Responsabilidade: Deletar um usuário.
 */
public class DeleteUserUseCase {
    private final UserRepository userRepository;

    public DeleteUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Executa a deleção de um usuário
     * @param id Nome do usuário
     * @return void
     * @throws IllegalArgumentException se email já existe ou dados são inválidos
     */
    public void execute(Long id) {
        // Verifica se o usuário existe
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        userRepository.deleteById(id);
    }
}
