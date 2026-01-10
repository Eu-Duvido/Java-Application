package com.euduvido.euduvido_api.infrastructure.persistence.entities;

import com.euduvido.euduvido_api.domain.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade JPA que mapeia a tabela de usuários no banco de dados.
 * Representa o mapeamento técnico da entidade de domínio User.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Converte entidade JPA para entidade de domínio
     */
    public User toDomain() {
        return User.createFromDatabase(id, name, email, password, profileImageUrl, createdAt);
    }

    /**
     * Cria entidade JPA a partir de entidade de domínio
     */
    public static UserEntity fromDomain(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setProfileImageUrl(user.getProfileImageUrl());
        entity.setCreatedAt(user.getCreatedAt());
        return entity;
    }
}

