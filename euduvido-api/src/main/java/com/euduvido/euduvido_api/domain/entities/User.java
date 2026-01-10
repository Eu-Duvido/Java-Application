package com.euduvido.euduvido_api.domain.entities;

import java.time.LocalDateTime;

/**
 * Entidade de domínio que representa um usuário do sistema.
 * Contém informações básicas do usuário.
 */
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String profileImageUrl;
    private LocalDateTime createdAt;

    // Construtor privado para garantir que a criação seja sempre através de factory
    private User(Long id, String name, String email, String password, String profileImageUrl, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
    }

    // Factory method para criar um novo usuário
    public static User create(String name, String email, String password, String profileImageUrl) {
        validateUserData(name, email, password);
        return new User(null, name, email, password, profileImageUrl, LocalDateTime.now());
    }

    // Factory method para recriar usuário do banco de dados
    public static User createFromDatabase(Long id, String name, String email, String password, String profileImageUrl, LocalDateTime createdAt) {
        return new User(id, name, email, password, profileImageUrl, createdAt);
    }

    // Validações de domínio
    private static void validateUserData(String name, String email, String password) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do usuário não pode ser vazio");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters para campos mutáveis
    public void updateProfile(String name, String profileImageUrl) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (profileImageUrl != null && !profileImageUrl.trim().isEmpty()) {
            this.profileImageUrl = profileImageUrl;
        }
    }
}

