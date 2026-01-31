package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.Invite;
import com.euduvido.euduvido_api.domain.repositories.InviteRepository;

import java.util.List;

public class ListInvitesUseCase {
    private final InviteRepository inviteRepository;

    public ListInvitesUseCase(InviteRepository inviteRepository) {
        this.inviteRepository = inviteRepository;
    }

    public List<Invite> listBySender(Long senderId) {
        return inviteRepository.findBySenderId(senderId);
    }

    public List<Invite> listByRecipient(Long recipientId) {
        return inviteRepository.findByRecipientId(recipientId);
    }
}

