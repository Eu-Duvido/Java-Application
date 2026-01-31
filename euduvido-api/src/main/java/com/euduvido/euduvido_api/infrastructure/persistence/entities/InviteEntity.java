package com.euduvido.euduvido_api.infrastructure.persistence.entities;

import com.euduvido.euduvido_api.domain.entities.Invite;
import com.euduvido.euduvido_api.domain.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidade JPA para representar um Invite no banco de dados
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invites")
public class InviteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(name = "message")
    private String message;

    @Column(name = "accepted", nullable = false)
    private Boolean accepted = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Converte entidade JPA para entidade de domínio.
     * Nota: Este método precisa receber os usuários pré-carregados.
     */
    public Invite toDomain(User sender, User recipient) {
        return Invite.createFromDatabase(id, sender, recipient, message, accepted, createdAt);
    }

    /**
     * Cria entidade JPA a partir de entidade de domínio
     */
    public static InviteEntity fromDomain(Invite invite) {
        InviteEntity entity = new InviteEntity();
        entity.setId(invite.getId());
        entity.setSenderId(invite.getSender() != null ? invite.getSender().getId() : null);
        entity.setRecipientId(invite.getRecipient() != null ? invite.getRecipient().getId() : null);
        entity.setMessage(invite.getMessage());
        entity.setAccepted(invite.getAccepted());
        entity.setCreatedAt(invite.getCreatedAt());
        return entity;
    }
}

