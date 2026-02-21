package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.usecases.CreateUserUseCase;
import com.euduvido.euduvido_api.application.usecases.DeleteUserUseCase;
import com.euduvido.euduvido_api.application.usecases.ListUserUseCase;
import com.euduvido.euduvido_api.application.usecases.UpdateUserUseCase;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.entrypoint.dtos.request.CreateUserRequest;
import com.euduvido.euduvido_api.entrypoint.dtos.request.UpdateUserRequest;
import com.euduvido.euduvido_api.entrypoint.dtos.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciar usuários.
 * Responsabilidade: Receber requisições HTTP e delegar para casos de uso.
 * NÃO contém regra de negócio.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final CreateUserUseCase createUserUseCase;
    private final ListUserUseCase listUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase,
                          ListUserUseCase listUserUseCase,
                          DeleteUserUseCase deleteUserUseCase,
                          UpdateUserUseCase updateUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.listUserUseCase = listUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
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

    /**
     * GET /api/v1/users
     * Listar todos usuários
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers() {
        List<User> users = listUserUseCase.execute();

        List<UserResponse> usersResponse = users.stream()
                .map(UserResponse::fromDomain)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(usersResponse);
    }

    /**
     * DELETE /api/v1/users/{id}
     * Deletar um usuário
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        deleteUserUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * UPDATE /api/v1/users/{id}
     * Atualizar um usuário
     */
    @PutMapping()
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        User updatedUser = updateUserUseCase.execute(request.getEmail(), request.getName(), request.getProfileImageUrl());

        return ResponseEntity.status(HttpStatus.OK)
                .body(UserResponse.fromDomain(updatedUser));
    }
}

