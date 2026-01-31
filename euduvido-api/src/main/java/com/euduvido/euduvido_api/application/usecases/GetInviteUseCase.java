package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.Invite;
import com.euduvido.euduvido_api.domain.repositories.InviteRepository;

import java.util.Optional;

public class GetInviteUseCase {
    private final InviteRepository inviteRepository;

    public GetInviteUseCase(InviteRepository inviteRepository) {
        this.inviteRepository = inviteRepository;
    }

    public Optional<Invite> execute(Long id) {
        return inviteRepository.findById(id);
    }
}

