package com.euduvido.euduvido_api.application.usecases.user;

import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Caso de uso: Criar um novo usuário.
 * Responsabilidade: Validar dados, encriptar senha e persistir novo usuário.
 */
public class CreateUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User execute(String name, String email, String password, String profileImageUrl) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        String encodedPassword = passwordEncoder.encode(password);
        User newUser = User.create(name, email, encodedPassword, profileImageUrl);
        return userRepository.save(newUser);
    }
}
