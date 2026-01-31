package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.Invite;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.repositories.InviteRepository;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CreateInviteUseCase Tests")
class CreateInviteUseCaseTest {

    private CreateInviteUseCase createInviteUseCase;

    @Mock
    private InviteRepository inviteRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        createInviteUseCase = new CreateInviteUseCase(inviteRepository, userRepository);
    }

    @Test
    @DisplayName("Should create an invite successfully")
    void testCreateInviteSuccess() {
        // Arrange
        User sender = User.createFromDatabase(1L, "Sender", "sender@test.com", "pass123", null, LocalDateTime.now());
        User recipient = User.createFromDatabase(2L, "Recipient", "recipient@test.com", "pass123", null, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(recipient));

        Invite expectedInvite = Invite.create(sender, recipient, "Test message");
        when(inviteRepository.save(any(Invite.class))).thenReturn(expectedInvite);

        // Act
        Invite result = createInviteUseCase.execute(1L, 2L, "Test message");

        // Assert
        assertNotNull(result);
        assertEquals(sender, result.getSender());
        assertEquals(recipient, result.getRecipient());
        assertEquals("Test message", result.getMessage());
        assertFalse(result.getAccepted());
    }

    @Test
    @DisplayName("Should throw exception when sender not found")
    void testCreateInviteSenderNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> createInviteUseCase.execute(1L, 2L, "Test message"));
    }

    @Test
    @DisplayName("Should throw exception when recipient not found")
    void testCreateInviteRecipientNotFound() {
        // Arrange
        User sender = User.createFromDatabase(1L, "Sender", "sender@test.com", "pass123", null, LocalDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> createInviteUseCase.execute(1L, 2L, "Test message"));
    }
}

