package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.Invite;
import com.euduvido.euduvido_api.domain.repositories.InviteRepository;

/**
 * Caso de uso para aceitar um convite
 */
public class AcceptInviteUseCase {
    private final InviteRepository inviteRepository;

    public AcceptInviteUseCase(InviteRepository inviteRepository) {
        this.inviteRepository = inviteRepository;
    }

    public Invite execute(Long id) {
        Invite invite = inviteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Convite não encontrado"));

        invite.accept();
        return inviteRepository.save(invite);
    }
}

