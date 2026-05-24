package com.euduvido.euduvido_api.application.services;

import java.time.Instant;

public interface TokenService {
    String generateToken(Long userId, String email);
    Instant extractExpiration(String token);
}
