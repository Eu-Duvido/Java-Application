package com.euduvido.euduvido_api.domain.entities;

import java.time.LocalDateTime;

/**
 * Entidade de domínio que representa um convite entre usuários.
 * Um convite é enviado por um usuário (sender) para outro (recipient) e pode ter uma mensagem.
 */
public class Invite {
    private Long id;
    private User sender;
    private User recipient;
    private String message;
    private Boolean accepted;
    private LocalDateTime createdAt;

    private Invite(Long id, User sender, User recipient, String message, Boolean accepted, LocalDateTime createdAt) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.accepted = accepted;
        this.createdAt = createdAt;
    }

    // Factory para criar novo convite
    public static Invite create(User sender, User recipient, String message) {
        validateInviteData(sender, recipient);
        return new Invite(null, sender, recipient, message, false, LocalDateTime.now());
    }

    // Factory para recriar do DB
    public static Invite createFromDatabase(Long id, User sender, User recipient, String message, Boolean accepted, LocalDateTime createdAt) {
        return new Invite(id, sender, recipient, message, accepted, createdAt);
    }

    private static void validateInviteData(User sender, User recipient) {
        if (sender == null) {
            throw new IllegalArgumentException("Convite deve ter um remetente");
        }
        if (recipient == null) {
            throw new IllegalArgumentException("Convite deve ter um destinatário");
        }
        if (sender.getId() != null && recipient.getId() != null && sender.getId().equals(recipient.getId())) {
            throw new IllegalArgumentException("Remetente e destinatário não podem ser o mesmo usuário");
        }
    }

    // Aceitar convite
    public void accept() {
        if (this.accepted) {
            throw new IllegalStateException("Convite já foi aceito");
        }
        this.accepted = true;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

