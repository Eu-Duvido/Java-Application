// ...existing code...
package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.Invite;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.repositories.InviteRepository;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;

/**
 * Caso de uso para criar um convite entre usuários.
 */
public class CreateInviteUseCase {
    private final InviteRepository inviteRepository;
    private final UserRepository userRepository;

    public CreateInviteUseCase(InviteRepository inviteRepository, UserRepository userRepository) {
        this.inviteRepository = inviteRepository;
        this.userRepository = userRepository;
    }

    public Invite execute(Long senderId, Long recipientId, String message) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Remetente não encontrado"));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("Destinatário não encontrado"));

        Invite invite = Invite.create(sender, recipient, message);
        return inviteRepository.save(invite);
    }
}

