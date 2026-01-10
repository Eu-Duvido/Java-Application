package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.usecases.CreateUserUseCase;
import com.euduvido.euduvido_api.entrypoint.dtos.request.CreateUserRequest;
import com.euduvido.euduvido_api.entrypoint.dtos.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para gerenciar usuários.
 * Responsabilidade: Receber requisições HTTP e delegar para casos de uso.
 * NÃO contém regra de negócio.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final CreateUserUseCase createUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    /**
     * POST /api/v1/users
     * Criar um novo usuário
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        var user = createUserUseCase.execute(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getProfileImageUrl()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.fromDomain(user));
    }
}

