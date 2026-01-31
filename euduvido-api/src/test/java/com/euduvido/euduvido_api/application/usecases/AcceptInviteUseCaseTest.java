package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.Invite;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.repositories.InviteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AcceptInviteUseCase Tests")
class AcceptInviteUseCaseTest {

    private AcceptInviteUseCase acceptInviteUseCase;

    @Mock
    private InviteRepository inviteRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        acceptInviteUseCase = new AcceptInviteUseCase(inviteRepository);
    }

    @Test
    @DisplayName("Should accept an invite successfully")
    void testAcceptInviteSuccess() {
        // Arrange
        User sender = User.createFromDatabase(1L, "Sender", "sender@test.com", "pass123", null, LocalDateTime.now());
        User recipient = User.createFromDatabase(2L, "Recipient", "recipient@test.com", "pass123", null, LocalDateTime.now());

        Invite invite = Invite.create(sender, recipient, "Test message");

        when(inviteRepository.findById(1L)).thenReturn(Optional.of(invite));
        when(inviteRepository.save(any(Invite.class))).thenReturn(invite);

        // Act
        Invite result = acceptInviteUseCase.execute(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.getAccepted());
    }

    @Test
    @DisplayName("Should throw exception when invite not found")
    void testAcceptInviteNotFound() {
        // Arrange
        when(inviteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> acceptInviteUseCase.execute(1L));
    }

    @Test
    @DisplayName("Should throw exception when trying to accept already accepted invite")
    void testAcceptAlreadyAcceptedInvite() {
        // Arrange
        User sender = User.createFromDatabase(1L, "Sender", "sender@test.com", "pass123", null, LocalDateTime.now());
        User recipient = User.createFromDatabase(2L, "Recipient", "recipient@test.com", "pass123", null, LocalDateTime.now());

        Invite invite = Invite.create(sender, recipient, "Test message");
        invite.accept(); // Primeiro aceitamento

        when(inviteRepository.findById(1L)).thenReturn(Optional.of(invite));

        // Act & Assert
        assertThrows(IllegalStateException.class,
            () -> acceptInviteUseCase.execute(1L));
    }
}

