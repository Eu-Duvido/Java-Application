package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.usecases.user.CreateUserUseCase;
import com.euduvido.euduvido_api.application.usecases.user.DeleteUserUseCase;
import com.euduvido.euduvido_api.application.usecases.user.ListUserUseCase;
import com.euduvido.euduvido_api.application.usecases.user.UpdateUserUseCase;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.entrypoint.dtos.request.CreateUserRequest;
import com.euduvido.euduvido_api.entrypoint.dtos.request.UpdateUserRequest;
import com.euduvido.euduvido_api.entrypoint.dtos.response.PageResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuários", description = "Cadastro e gerenciamento de usuários")
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

    @Operation(summary = "Criar usuário", description = "Registra novo usuário; não requer autenticação")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    @SecurityRequirements
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        var user = createUserUseCase.execute(
                request.getName(), request.getEmail(),
                request.getPassword(), request.getProfileImageUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromDomain(user));
    }

    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários paginados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = listUserUseCase.execute(page, size);
        return ResponseEntity.ok(PageResponse.fromDomain(result, UserResponse::fromDomain));
    }

    @Operation(summary = "Deletar usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário removido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        deleteUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        User updatedUser = updateUserUseCase.execute(
                request.getEmail(), request.getName(), request.getProfileImageUrl());
        return ResponseEntity.status(HttpStatus.OK).body(UserResponse.fromDomain(updatedUser));
    }
}
