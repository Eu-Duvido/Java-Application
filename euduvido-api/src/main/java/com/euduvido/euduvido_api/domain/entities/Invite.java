package com.euduvido.euduvido_api.domain.entities;

import java.time.LocalDateTime;

public class Invite {
    private Long id;
    private User sender;
    private User recipient;
    private String message;
    private Boolean accepted;

    private Invite(Long id, User sender, User recipient, String message, Boolean accepted) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.accepted = accepted;
    }

    // Factory para criar novo convite
    public static Invite create(User sender, User recipient, String message) {
        validateInviteData(sender, recipient);
        return new Invite(null, sender, recipient, message, false);
    }

    // Factory para recriar do DB
    public static Invite createFromDatabase(Long id, User sender, User recipient, String message, Boolean accepted) {
        return new Invite(id, sender, recipient, message, accepted);
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

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }
    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }
    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getAccepted() {
        return accepted;
    }
    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
