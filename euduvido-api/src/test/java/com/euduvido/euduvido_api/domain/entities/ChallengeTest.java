package com.euduvido.euduvido_api.domain.entities;

import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a entidade Challenge (Domínio).
 * Valida as regras de negócio da entidade.
 */
class ChallengeTest {

    @Test
    void shouldCreateChallengeWithValidData() {
        // Arrange
        String title = "Desafio Radical";
        String description = "Pule de paraquedas";
        User creator = User.create("João", "joao@email.com", "senha123", null);
        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        Boolean locationRequired = true;

        // Act
        Challenge challenge = Challenge.create(title, description, creator, deadline, locationRequired);

        // Assert
        assertNotNull(challenge);
        assertEquals(title, challenge.getTitle());
        assertEquals(description, challenge.getDescription());
        assertEquals(creator, challenge.getCreator());
        assertEquals(deadline, challenge.getDeadline());
        assertEquals(locationRequired, challenge.getLocationRequired());
        assertEquals(ChallengeStatus.PENDING, challenge.getStatus());
        assertNull(challenge.getId());
        assertNotNull(challenge.getCreatedAt());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsEmpty() {
        // Arrange
        User creator = User.create("João", "joao@email.com", "senha123", null);
        LocalDateTime deadline = LocalDateTime.now().plusDays(7);

        // Assert & Act
        assertThrows(IllegalArgumentException.class, () ->
                Challenge.create("", "Descrição", creator, deadline, false)
        );
    }

    @Test
    void shouldThrowExceptionWhenDeadlineIsInPast() {
        // Arrange
        User creator = User.create("João", "joao@email.com", "senha123", null);
        LocalDateTime pastDeadline = LocalDateTime.now().minusDays(1);

        // Assert & Act
        assertThrows(IllegalArgumentException.class, () ->
                Challenge.create("Título", "Descrição", creator, pastDeadline, false)
        );
    }

    @Test
    void shouldActivateChallenge() {
        // Arrange
        User creator = User.create("João", "joao@email.com", "senha123", null);
        Challenge challenge = Challenge.create("Título", "Descrição", creator,
                LocalDateTime.now().plusDays(7), false);

        // Act
        challenge.activate();

        // Assert
        assertEquals(ChallengeStatus.ACTIVE, challenge.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenActivatingNonPendingChallenge() {
        // Arrange
        User creator = User.create("João", "joao@email.com", "senha123", null);
        Challenge challenge = Challenge.create("Título", "Descrição", creator,
                LocalDateTime.now().plusDays(7), false);
        challenge.activate();

        // Assert & Act
        assertThrows(IllegalStateException.class, challenge::activate);
    }

    @Test
    void shouldCompleteChallenge() {
        // Arrange
        User creator = User.create("João", "joao@email.com", "senha123", null);
        Challenge challenge = Challenge.create("Título", "Descrição", creator,
                LocalDateTime.now().plusDays(7), false);
        challenge.activate();

        // Act
        challenge.complete();

        // Assert
        assertEquals(ChallengeStatus.COMPLETED, challenge.getStatus());
    }

    @Test
    void shouldCheckIfChallengeIsExpired() {
        // Arrange
        User creator = User.create("João", "joao@email.com", "senha123", null);
        LocalDateTime pastDeadline = LocalDateTime.now().minusHours(1);
        Challenge challenge = Challenge.createFromDatabase(1L, "Título", "Descrição",
                creator, pastDeadline, ChallengeStatus.ACTIVE, false, LocalDateTime.now());

        // Act & Assert
        assertTrue(challenge.isExpired());
    }

    @Test
    void shouldCheckIfChallengeIsNotExpired() {
        // Arrange
        User creator = User.create("João", "joao@email.com", "senha123", null);
        LocalDateTime futureDeadline = LocalDateTime.now().plusDays(7);
        Challenge challenge = Challenge.createFromDatabase(1L, "Título", "Descrição",
                creator, futureDeadline, ChallengeStatus.ACTIVE, false, LocalDateTime.now());

        // Act & Assert
        assertFalse(challenge.isExpired());
    }
}

