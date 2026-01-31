package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.repositories.InviteRepository;

public class DeleteInviteUseCase {
    private final InviteRepository inviteRepository;

    public DeleteInviteUseCase(InviteRepository inviteRepository) {
        this.inviteRepository = inviteRepository;
    }

    public void execute(Long id) {
        inviteRepository.deleteById(id);
    }
}

