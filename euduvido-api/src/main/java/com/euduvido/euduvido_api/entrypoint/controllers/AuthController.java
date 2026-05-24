package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.usecases.auth.LoginUseCase;
import com.euduvido.euduvido_api.entrypoint.dtos.request.LoginRequest;
import com.euduvido.euduvido_api.entrypoint.dtos.response.LoginResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Login e emissão de token JWT")
public class AuthController {

    private final LoginUseCase loginUseCase;

    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @Operation(summary = "Autenticar usuário", description = "Valida credenciais e retorna token JWT de 24 h")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Credenciais inválidas ou campo obrigatório ausente")
    })
    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginUseCase.Result result = loginUseCase.execute(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(
                result.token(),
                result.expiresAt(),
                UserResponse.fromDomain(result.user())
        ));
    }
}
