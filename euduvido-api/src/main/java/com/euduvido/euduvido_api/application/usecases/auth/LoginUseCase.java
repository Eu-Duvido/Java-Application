package com.euduvido.euduvido_api.application.usecases.auth;

import com.euduvido.euduvido_api.application.services.TokenService;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public LoginUseCase(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public record Result(String token, Instant expiresAt, User user) {}

    public Result execute(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }
        String token = tokenService.generateToken(user.getId(), user.getEmail());
        Instant expiresAt = tokenService.extractExpiration(token);
        return new Result(token, expiresAt, user);
    }
}
