package com.euduvido.euduvido_api.domain.entities;

import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.domain.enums.Difficulty;
import com.euduvido.euduvido_api.domain.enums.GoalType;
import com.euduvido.euduvido_api.domain.enums.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProofTest {

    private ChallengeParticipation acceptedParticipation;

    @BeforeEach
    void setUp() {
        User user = User.createFromDatabase(1L, "Bob", "bob@uni.br", "pass123", null);
        Challenge challenge = Challenge.createFromDatabase(1L, "Estude 2h", "Estudar",
                Difficulty.EASY, "Calc", GoalType.HOURS, 2,
                user, LocalDateTime.now().plusDays(3),
                ChallengeStatus.ACTIVE, false, LocalDateTime.now(), List.of());
        acceptedParticipation = ChallengeParticipation.create(user, challenge);
        acceptedParticipation.accept();
    }

    @Test
    void create_returnsValidProof() {
        Proof proof = Proof.create(acceptedParticipation, "http://cdn/img.jpg", MediaType.PHOTO, null, null);
        assertNotNull(proof);
        assertEquals("http://cdn/img.jpg", proof.getMediaUrl());
        assertEquals(MediaType.PHOTO, proof.getMediaType());
        assertFalse(proof.isApproved());
        assertNull(proof.getRejectionReason());
        assertNotNull(proof.getSubmittedAt());
    }

    @Test
    void create_throwsWhenParticipationNull() {
        assertThrows(IllegalArgumentException.class,
                () -> Proof.create(null, "http://cdn/img.jpg", MediaType.PHOTO, null, null));
    }

    @Test
    void create_throwsWhenMediaUrlBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> Proof.create(acceptedParticipation, "", MediaType.PHOTO, null, null));
    }

    @Test
    void create_throwsWhenMediaTypeNull() {
        assertThrows(IllegalArgumentException.class,
                () -> Proof.create(acceptedParticipation, "http://cdn/img.jpg", null, null, null));
    }

    @Test
    void approve_marksApprovedAndCompletesParticipation() {
        Proof proof = Proof.create(acceptedParticipation, "http://cdn/img.jpg", MediaType.PHOTO, null, null);
        proof.approve();
        assertTrue(proof.isApproved());
        assertNull(proof.getRejectionReason());
    }

    @Test
    void approve_throwsWhenAlreadyApproved() {
        Proof proof = Proof.create(acceptedParticipation, "http://cdn/img.jpg", MediaType.PHOTO, null, null);
        proof.approve();
        assertThrows(IllegalStateException.class, proof::approve);
    }

    @Test
    void reject_setsRejectionReason() {
        Proof proof = Proof.create(acceptedParticipation, "http://cdn/img.jpg", MediaType.PHOTO, null, null);
        proof.reject("Imagem ilegível");
        assertEquals("Imagem ilegível", proof.getRejectionReason());
        assertFalse(proof.isApproved());
    }

    @Test
    void reject_throwsWhenAlreadyApproved() {
        Proof proof = Proof.create(acceptedParticipation, "http://cdn/img.jpg", MediaType.PHOTO, null, null);
        proof.approve();
        assertThrows(IllegalStateException.class, () -> proof.reject("too late"));
    }

    @Test
    void aiFields_canBeSetAndRead() {
        Proof proof = Proof.create(acceptedParticipation, "http://cdn/img.jpg", MediaType.PHOTO, null, null);
        proof.setAiValid(true);
        proof.setAiConfidence(0.92);
        proof.setAiReason("Imagem mostra evidência clara");
        assertTrue(proof.getAiValid());
        assertEquals(0.92, proof.getAiConfidence());
        assertEquals("Imagem mostra evidência clara", proof.getAiReason());
    }
}
