package com.euduvido.euduvido_api.domain.entities;

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

    public User() {
    }

    private User(Long id, String name, String email, String password, String profileImageUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
    }

    public static User create(String name, String email, String password, String profileImageUrl) {
        validateUserData(name, email, password);
        return new User(null, name, email, password, profileImageUrl);
    }

    public static User createFromDatabase(Long id, String name, String email, String password, String profileImageUrl) {
        return new User(id, name, email, password, profileImageUrl);
    }

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

    public void updateProfile(String name, String profileImageUrl) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (profileImageUrl != null && !profileImageUrl.trim().isEmpty()) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}

