package com.euduvido.euduvido_api.entrypoint.dtos.request;

import jakarta.validation.constraints.NotNull;

/**
 * DTO de requisição para criar convite
 */
public class CreateInviteRequest {
    @NotNull
    private Long senderId;
    @NotNull
    private Long recipientId;
    private String message;

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
