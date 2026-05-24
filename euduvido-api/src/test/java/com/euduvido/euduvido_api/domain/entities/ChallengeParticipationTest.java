package com.euduvido.euduvido_api.domain.entities;

import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.domain.enums.Difficulty;
import com.euduvido.euduvido_api.domain.enums.GoalType;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeParticipationTest {

    private User user;
    private Challenge challenge;

    @BeforeEach
    void setUp() {
        user = User.createFromDatabase(1L, "Alice", "alice@uni.br", "senha123", null);
        challenge = Challenge.createFromDatabase(1L, "Leia 50 páginas", "Leitura acadêmica",
                Difficulty.MEDIUM, "Literatura", GoalType.PAGES, 50,
                user, LocalDateTime.now().plusDays(7),
                ChallengeStatus.ACTIVE, false, LocalDateTime.now(), List.of());
    }

    @Test
    void create_setsInvitedStatus() {
        ChallengeParticipation p = ChallengeParticipation.create(user, challenge);
        assertEquals(ParticipationStatus.INVITED, p.getStatus());
        assertEquals(0, p.getProgress());
    }

    @Test
    void create_throwsWhenUserNull() {
        assertThrows(IllegalArgumentException.class,
                () -> ChallengeParticipation.create(null, challenge));
    }

    @Test
    void create_throwsWhenChallengeNull() {
        assertThrows(IllegalArgumentException.class,
                () -> ChallengeParticipation.create(user, null));
    }

    @Test
    void accept_changesStatusToAccepted() {
        ChallengeParticipation p = ChallengeParticipation.create(user, challenge);
        p.accept();
        assertEquals(ParticipationStatus.ACCEPTED, p.getStatus());
    }

    @Test
    void accept_throwsWhenNotInvited() {
        ChallengeParticipation p = ChallengeParticipation.create(user, challenge);
        p.accept();
        assertThrows(IllegalStateException.class, p::accept);
    }

    @Test
    void refuse_changesStatusToRefused() {
        ChallengeParticipation p = ChallengeParticipation.create(user, challenge);
        p.refuse();
        assertEquals(ParticipationStatus.REFUSED, p.getStatus());
    }

    @Test
    void refuse_throwsWhenNotInvited() {
        ChallengeParticipation p = ChallengeParticipation.create(user, challenge);
        p.accept();
        assertThrows(IllegalStateException.class, p::refuse);
    }

    @Test
    void complete_changesStatusToCompleted() {
        ChallengeParticipation p = ChallengeParticipation.create(user, challenge);
        p.accept();
        p.complete();
        assertEquals(ParticipationStatus.COMPLETED, p.getStatus());
    }

    @Test
    void complete_throwsWhenNotAccepted() {
        ChallengeParticipation p = ChallengeParticipation.create(user, challenge);
        assertThrows(IllegalStateException.class, p::complete);
    }

    @Test
    void updateProgress_setsProgress() {
        ChallengeParticipation p = ChallengeParticipation.create(user, challenge);
        p.accept();
        p.updateProgress(30);
        assertEquals(30, p.getProgress());
    }

    @Test
    void updateProgress_throwsWhenNegative() {
        ChallengeParticipation p = ChallengeParticipation.create(user, challenge);
        p.accept();
        assertThrows(IllegalArgumentException.class, () -> p.updateProgress(-1));
    }

    @Test
    void updateProgress_throwsWhenExceedsGoal() {
        ChallengeParticipation p = ChallengeParticipation.create(user, challenge);
        p.accept();
        assertThrows(IllegalArgumentException.class, () -> p.updateProgress(51));
    }

    @Test
    void updateProgress_throwsWhenNotAccepted() {
        ChallengeParticipation p = ChallengeParticipation.create(user, challenge);
        assertThrows(IllegalStateException.class, () -> p.updateProgress(10));
    }
}
