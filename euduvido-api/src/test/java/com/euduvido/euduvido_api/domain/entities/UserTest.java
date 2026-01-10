package com.euduvido.euduvido_api.domain.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a entidade User (Domínio).
 * Valida as regras de negócio da entidade.
 */
class UserTest {

    @Test
    void shouldCreateUserWithValidData() {
        // Arrange
        String name = "João Silva";
        String email = "joao@email.com";
        String password = "senha123";
        String profileImageUrl = "https://example.com/photo.jpg";

        // Act
        User user = User.create(name, email, password, profileImageUrl);

        // Assert
        assertNotNull(user);
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(profileImageUrl, user.getProfileImageUrl());
        assertNull(user.getId()); // Novo usuário ainda não tem ID
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        // Assert & Act
        assertThrows(IllegalArgumentException.class, () ->
                User.create("", "joao@email.com", "senha123", null)
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Assert & Act
        assertThrows(IllegalArgumentException.class, () ->
                User.create("João", "email-invalido", "senha123", null)
        );
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsTooShort() {
        // Assert & Act
        assertThrows(IllegalArgumentException.class, () ->
                User.create("João", "joao@email.com", "123", null)
        );
    }

    @Test
    void shouldUpdateUserProfile() {
        // Arrange
        User user = User.create("João", "joao@email.com", "senha123", null);
        String newName = "João Silva";
        String newImageUrl = "https://example.com/newphoto.jpg";

        // Act
        user.updateProfile(newName, newImageUrl);

        // Assert
        assertEquals(newName, user.getName());
        assertEquals(newImageUrl, user.getProfileImageUrl());
    }
}

