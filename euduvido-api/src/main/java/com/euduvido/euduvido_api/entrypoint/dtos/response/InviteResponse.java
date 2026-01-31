package com.euduvido.euduvido_api.entrypoint.dtos.response;

import com.euduvido.euduvido_api.domain.entities.Invite;

import java.time.LocalDateTime;

/**
 * DTO de resposta para Invite
 */
public class InviteResponse {
    private Long id;
    private Long senderId;
    private Long recipientId;
    private String message;
    private Boolean accepted;
    private LocalDateTime createdAt;

    public static InviteResponse fromDomain(Invite invite) {
        InviteResponse r = new InviteResponse();
        r.id = invite.getId();
        r.senderId = invite.getSender() != null ? invite.getSender().getId() : null;
        r.recipientId = invite.getRecipient() != null ? invite.getRecipient().getId() : null;
        r.message = invite.getMessage();
        r.accepted = invite.getAccepted();
        r.createdAt = invite.getCreatedAt();
        return r;
    }

    // Getters
    public Long getId() { return id; }
    public Long getSenderId() { return senderId; }
    public Long getRecipientId() { return recipientId; }
    public String getMessage() { return message; }
    public Boolean getAccepted() { return accepted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
