package com.euduvido.euduvido_api.infrastructure.repositories;

import com.euduvido.euduvido_api.domain.entities.Invite;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.repositories.InviteRepository;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;
import com.euduvido.euduvido_api.infrastructure.persistence.entities.InviteEntity;
import com.euduvido.euduvido_api.infrastructure.persistence.repositories.InviteJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementação do repositório de domínio InviteRepository.
 * Adapta o Spring Data JPA para o contrato do domínio.
 */
@Component
public class InviteRepositoryImpl implements InviteRepository {
    private final InviteJpaRepository jpaRepository;
    private final UserRepository userRepository;

    public InviteRepositoryImpl(InviteJpaRepository jpaRepository, UserRepository userRepository) {
        this.jpaRepository = jpaRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Invite save(Invite invite) {
        InviteEntity entity = InviteEntity.fromDomain(invite);
        InviteEntity saved = jpaRepository.save(entity);

        // Carregar os usuários associados para retornar a entidade de domínio completa
        User sender = userRepository.findById(saved.getSenderId()).orElse(null);
        User recipient = userRepository.findById(saved.getRecipientId()).orElse(null);

        return saved.toDomain(sender, recipient);
    }

    @Override
    public Optional<Invite> findById(Long id) {
        return jpaRepository.findById(id).flatMap(entity -> {
            User sender = userRepository.findById(entity.getSenderId()).orElse(null);
            User recipient = userRepository.findById(entity.getRecipientId()).orElse(null);
            return Optional.of(entity.toDomain(sender, recipient));
        });
    }

    @Override
    public List<Invite> findBySenderId(Long senderId) {
        User sender = userRepository.findById(senderId).orElse(null);

        return jpaRepository.findBySenderId(senderId).stream()
                .map(entity -> {
                    User recipient = userRepository.findById(entity.getRecipientId()).orElse(null);
                    return entity.toDomain(sender, recipient);
                })
                .toList();
    }

    @Override
    public List<Invite> findByRecipientId(Long recipientId) {
        User recipient = userRepository.findById(recipientId).orElse(null);

        return jpaRepository.findByRecipientId(recipientId).stream()
                .map(entity -> {
                    User sender = userRepository.findById(entity.getSenderId()).orElse(null);
                    return entity.toDomain(sender, recipient);
                })
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Invite> findAll() {
        return jpaRepository.findAll().stream()
                .map(entity -> {
                    User sender = userRepository.findById(entity.getSenderId()).orElse(null);
                    User recipient = userRepository.findById(entity.getRecipientId()).orElse(null);
                    return entity.toDomain(sender, recipient);
                })
                .toList();
    }
}

