package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;

/**
 * Caso de uso: Criar um novo usuário.
 * Responsabilidade: Validar dados e persistir novo usuário.
 */
public class CreateUserUseCase {
    private final UserRepository userRepository;

    public CreateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Executa a criação de um novo usuário
     * @param name Nome do usuário
     * @param email Email do usuário (deve ser único)
     * @param password Senha do usuário
     * @param profileImageUrl URL da imagem de perfil (opcional)
     * @return Usuário criado
     * @throws IllegalArgumentException se email já existe ou dados são inválidos
     */
    public User execute(String name, String email, String password, String profileImageUrl) {
        // Validar se email já existe
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Criar usuário (validações de domínio ocorrem aqui)
        User newUser = User.create(name, email, password, profileImageUrl);

        // Persistir usuário
        return userRepository.save(newUser);
    }
}

