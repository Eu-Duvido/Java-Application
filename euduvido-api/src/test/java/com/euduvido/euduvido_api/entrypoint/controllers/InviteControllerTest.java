package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.usecases.*;
import com.euduvido.euduvido_api.domain.entities.Invite;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.entrypoint.dtos.request.CreateInviteRequest;
import com.euduvido.euduvido_api.entrypoint.dtos.response.InviteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("InviteController Tests")
class InviteControllerTest {

    private InviteController inviteController;

    @Mock
    private CreateInviteUseCase createInviteUseCase;

    @Mock
    private GetInviteUseCase getInviteUseCase;

    @Mock
    private ListInvitesUseCase listInvitesUseCase;

    @Mock
    private DeleteInviteUseCase deleteInviteUseCase;

    @Mock
    private AcceptInviteUseCase acceptInviteUseCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        inviteController = new InviteController(
            createInviteUseCase,
            getInviteUseCase,
            listInvitesUseCase,
            deleteInviteUseCase,
            acceptInviteUseCase
        );
    }

    @Test
    @DisplayName("Should create invite and return 201 CREATED")
    void testCreateInvite() {
        // Arrange
        CreateInviteRequest request = new CreateInviteRequest();
        request.setSenderId(1L);
        request.setRecipientId(2L);
        request.setMessage("Test");

        User sender = User.createFromDatabase(1L, "Sender", "sender@test.com", "pass123", null, LocalDateTime.now());
        User recipient = User.createFromDatabase(2L, "Recipient", "recipient@test.com", "pass123", null, LocalDateTime.now());

        Invite invite = Invite.create(sender, recipient, "Test");

        when(createInviteUseCase.execute(1L, 2L, "Test")).thenReturn(invite);

        // Act
        ResponseEntity<InviteResponse> response = inviteController.createInvite(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getSenderId());
        assertEquals(2L, response.getBody().getRecipientId());
    }

    @Test
    @DisplayName("Should get invite by id")
    void testGetInvite() {
        // Arrange
        User sender = User.createFromDatabase(1L, "Sender", "sender@test.com", "pass123", null, LocalDateTime.now());
        User recipient = User.createFromDatabase(2L, "Recipient", "recipient@test.com", "pass123", null, LocalDateTime.now());
        Invite invite = Invite.create(sender, recipient, "Test");

        when(getInviteUseCase.execute(1L)).thenReturn(Optional.of(invite));

        // Act
        ResponseEntity<InviteResponse> response = inviteController.getInvite(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should return 404 when invite not found")
    void testGetInviteNotFound() {
        // Arrange
        when(getInviteUseCase.execute(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<InviteResponse> response = inviteController.getInvite(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should list invites by sender")
    void testListBySender() {
        // Arrange
        User sender = User.createFromDatabase(1L, "Sender", "sender@test.com", "pass123", null, LocalDateTime.now());
        User recipient = User.createFromDatabase(2L, "Recipient", "recipient@test.com", "pass123", null, LocalDateTime.now());

        List<Invite> invites = new ArrayList<>();
        invites.add(Invite.create(sender, recipient, "Test1"));

        when(listInvitesUseCase.listBySender(1L)).thenReturn(invites);

        // Act
        ResponseEntity<List<InviteResponse>> response = inviteController.listBySender(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should delete invite")
    void testDeleteInvite() {
        // Act
        ResponseEntity<Void> response = inviteController.deleteInvite(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deleteInviteUseCase, times(1)).execute(1L);
    }

    @Test
    @DisplayName("Should accept invite")
    void testAcceptInvite() {
        // Arrange
        User sender = User.createFromDatabase(1L, "Sender", "sender@test.com", "pass123", null, LocalDateTime.now());
        User recipient = User.createFromDatabase(2L, "Recipient", "recipient@test.com", "pass123", null, LocalDateTime.now());
        Invite invite = Invite.create(sender, recipient, "Test");

        when(acceptInviteUseCase.execute(1L)).thenReturn(invite);

        // Act
        ResponseEntity<InviteResponse> response = inviteController.acceptInvite(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}

