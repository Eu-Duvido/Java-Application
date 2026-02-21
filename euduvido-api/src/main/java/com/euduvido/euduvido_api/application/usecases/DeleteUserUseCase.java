package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.repositories.UserRepository;

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
     * @throws IllegalArgumentException se email já existe ou dados são inválidos
     */
    public void execute(Long id) {
        userRepository.deleteById(id);
    }
}
