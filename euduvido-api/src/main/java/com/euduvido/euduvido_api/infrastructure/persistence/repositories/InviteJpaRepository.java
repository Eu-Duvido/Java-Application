package com.euduvido.euduvido_api.infrastructure.persistence.repositories;

import com.euduvido.euduvido_api.infrastructure.persistence.entities.InviteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositório JPA para Invite
 */
@Repository
public interface InviteJpaRepository extends JpaRepository<InviteEntity, Long> {
    List<InviteEntity> findBySenderId(Long senderId);
    List<InviteEntity> findByRecipientId(Long recipientId);
}

