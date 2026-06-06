package com.euduvido.euduvido_api.domain.entities;

import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.domain.enums.Difficulty;
import com.euduvido.euduvido_api.domain.enums.GoalType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeTest {

    private static final Difficulty DEFAULT_DIFFICULTY = Difficulty.MEDIUM;
    private static final GoalType DEFAULT_GOAL_TYPE = GoalType.PAGES;
    private static final int DEFAULT_GOAL_VALUE = 50;

    @Test
    void shouldCreateChallengeWithValidData() {
        User creator = User.create("João", "joao@email.com", "senha123", null);
        LocalDateTime deadline = LocalDateTime.now().plusDays(7);

        Challenge challenge = Challenge.create("Desafio Radical", "Pule de paraquedas",
                creator, deadline, true, DEFAULT_DIFFICULTY, "Educação Física",
                DEFAULT_GOAL_TYPE, DEFAULT_GOAL_VALUE);

        assertNotNull(challenge);
        assertEquals("Desafio Radical", challenge.getTitle());
        assertEquals(creator, challenge.getCreator());
        assertEquals(deadline, challenge.getDeadline());
        assertEquals(ChallengeStatus.ACTIVE, challenge.getStatus());
        assertEquals(DEFAULT_DIFFICULTY, challenge.getDifficulty());
        assertEquals(DEFAULT_GOAL_VALUE, challenge.getGoalValue());
        assertNull(challenge.getId());
        assertNotNull(challenge.getCreatedAt());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsEmpty() {
        User creator = User.create("João", "joao@email.com", "senha123", null);
        assertThrows(IllegalArgumentException.class, () ->
                Challenge.create("", "Descrição", creator, LocalDateTime.now().plusDays(7),
                        false, DEFAULT_DIFFICULTY, null, DEFAULT_GOAL_TYPE, DEFAULT_GOAL_VALUE));
    }

    @Test
    void shouldThrowExceptionWhenDeadlineIsInPast() {
        User creator = User.create("João", "joao@email.com", "senha123", null);
        assertThrows(IllegalArgumentException.class, () ->
                Challenge.create("Título", "Descrição", creator, LocalDateTime.now().minusDays(1),
                        false, DEFAULT_DIFFICULTY, null, DEFAULT_GOAL_TYPE, DEFAULT_GOAL_VALUE));
    }

    @Test
    void shouldActivateChallenge() {
        User creator = User.create("João", "joao@email.com", "senha123", null);
        Challenge challenge = Challenge.create("Título", "Descrição", creator,
                LocalDateTime.now().plusDays(7), false, DEFAULT_DIFFICULTY, null,
                DEFAULT_GOAL_TYPE, DEFAULT_GOAL_VALUE);

        challenge.setStatus(ChallengeStatus.PENDING);
        challenge.activate();

        assertEquals(ChallengeStatus.ACTIVE, challenge.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenActivatingNonPendingChallenge() {
        User creator = User.create("João", "joao@email.com", "senha123", null);
        Challenge challenge = Challenge.create("Título", "Descrição", creator,
                LocalDateTime.now().plusDays(7), false, DEFAULT_DIFFICULTY, null,
                DEFAULT_GOAL_TYPE, DEFAULT_GOAL_VALUE);
        assertThrows(IllegalStateException.class, challenge::activate);
    }

    @Test
    void shouldCompleteChallenge() {
        User creator = User.create("João", "joao@email.com", "senha123", null);
        Challenge challenge = Challenge.create("Título", "Descrição", creator,
                LocalDateTime.now().plusDays(7), false, DEFAULT_DIFFICULTY, null,
                DEFAULT_GOAL_TYPE, DEFAULT_GOAL_VALUE);
        challenge.complete();

        assertEquals(ChallengeStatus.COMPLETED, challenge.getStatus());
    }

    @Test
    void shouldCheckIfChallengeIsExpired() {
        User creator = User.create("João", "joao@email.com", "senha123", null);
        LocalDateTime pastDeadline = LocalDateTime.now().minusHours(1);
        Challenge challenge = Challenge.createFromDatabase(1L, "Título", "Descrição",
                null, null, null, null, creator, pastDeadline, ChallengeStatus.ACTIVE,
                false, LocalDateTime.now(), null);

        assertTrue(challenge.isExpired());
    }

    @Test
    void shouldCheckIfChallengeIsNotExpired() {
        User creator = User.create("João", "joao@email.com", "senha123", null);
        LocalDateTime futureDeadline = LocalDateTime.now().plusDays(7);
        Challenge challenge = Challenge.createFromDatabase(1L, "Título", "Descrição",
                null, null, null, null, creator, futureDeadline, ChallengeStatus.ACTIVE,
                false, LocalDateTime.now(), null);

        assertFalse(challenge.isExpired());
    }
}
