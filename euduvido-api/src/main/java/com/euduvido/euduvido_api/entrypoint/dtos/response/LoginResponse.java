package com.euduvido.euduvido_api.entrypoint.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/** DTO de response para autenticação JWT. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Instant expiresAt;
    private UserResponse user;
}
