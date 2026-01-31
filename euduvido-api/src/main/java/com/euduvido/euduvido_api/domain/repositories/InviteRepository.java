package com.euduvido.euduvido_api.domain.repositories;

import com.euduvido.euduvido_api.domain.entities.Invite;
import java.util.List;
import java.util.Optional;

/**
 * Interface do repositório de domínio para Invite
 */
public interface InviteRepository {
    Invite save(Invite invite);
    Optional<Invite> findById(Long id);
    List<Invite> findBySenderId(Long senderId);
    List<Invite> findByRecipientId(Long recipientId);
    void deleteById(Long id);
    List<Invite> findAll();
}

