package com.euduvido.euduvido_api.application.usecases.proof;

import com.euduvido.euduvido_api.application.exception.EvidenceValidationException;
import com.euduvido.euduvido_api.application.services.AiValidationService;
import com.euduvido.euduvido_api.application.services.EvidenceValidationResult;
import com.euduvido.euduvido_api.application.services.FileStorageService;
import com.euduvido.euduvido_api.application.services.StoredFile;
import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.enums.*;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;
import com.euduvido.euduvido_api.domain.repositories.ProofRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmitProofUseCaseTest {

    @Mock ProofRepository proofRepository;
    @Mock ChallengeParticipationRepository participationRepository;
    @Mock FileStorageService fileStorageService;
    @Mock AiValidationService aiValidationService;

    @InjectMocks SubmitProofUseCase useCase;

    private ChallengeParticipation participation;

    private static final EvidenceValidationResult VALID_IMAGE =
            new EvidenceValidationResult(200, "study_evidence", "Evidência válida", true);

    @BeforeEach
    void setUp() {
        User user = User.createFromDatabase(1L, "Carlos", "carlos@uni.br", "pass123", null);
        Challenge challenge = Challenge.createFromDatabase(1L, "Prova foto", "Foto do livro",
                Difficulty.EASY, "Lit", GoalType.PAGES, 10,
                user, LocalDateTime.now().plusDays(3),
                ChallengeStatus.ACTIVE, false, LocalDateTime.now(), List.of());
        participation = ChallengeParticipation.createFromDatabase(1L, user, challenge,
                ParticipationStatus.ACCEPTED, 0);
    }

    // ─── Image validation ────────────────────────────────────────────────────────

    @Test
    void execute_returns201_whenImageIsValidStudyEvidence() {
        when(participationRepository.findById(1L)).thenReturn(Optional.of(participation));
        when(aiValidationService.validateProofImage(any(), anyString(), anyString(), anyString()))
                .thenReturn(VALID_IMAGE);
        when(fileStorageService.store(any(), anyString()))
                .thenReturn(new StoredFile("photo.jpg", "http://cdn/photo.jpg"));
        when(proofRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Proof result = useCase.execute(1L, new byte[]{1, 2, 3}, "photo.jpg",
                MediaType.PHOTO, null, null, null);

        assertNotNull(result);
        assertEquals("http://cdn/photo.jpg", result.getMediaUrl());
        assertTrue(result.getAiValid());
    }

    @Test
    void execute_throwsEvidenceValidationException_status400_whenImageIsNotStudyRelated() {
        when(participationRepository.findById(1L)).thenReturn(Optional.of(participation));
        when(aiValidationService.validateProofImage(any(), anyString(), anyString(), anyString()))
                .thenReturn(new EvidenceValidationResult(400, "not_study_related",
                        "Imagem não contém evidência de estudo", false));

        EvidenceValidationException ex = assertThrows(EvidenceValidationException.class, () ->
                useCase.execute(1L, new byte[]{1, 2, 3}, "photo.jpg", MediaType.PHOTO, null, null, null));

        assertEquals(400, ex.getResult().status());
        assertEquals("not_study_related", ex.getResult().category());
        assertFalse(ex.getResult().isValid());
    }

    @Test
    void execute_throwsEvidenceValidationException_status450_whenImageIsInappropriate() {
        when(participationRepository.findById(1L)).thenReturn(Optional.of(participation));
        when(aiValidationService.validateProofImage(any(), anyString(), anyString(), anyString()))
                .thenReturn(new EvidenceValidationResult(450, "inappropriate_content",
                        "Conteúdo impróprio detectado na imagem", false));

        EvidenceValidationException ex = assertThrows(EvidenceValidationException.class, () ->
                useCase.execute(1L, new byte[]{1, 2, 3}, "photo.jpg", MediaType.PHOTO, null, null, null));

        assertEquals(450, ex.getResult().status());
        assertEquals("inappropriate_content", ex.getResult().category());
        assertFalse(ex.getResult().isValid());
    }

    @Test
    void execute_propagatesException_whenAiServiceFails() {
        when(participationRepository.findById(1L)).thenReturn(Optional.of(participation));
        when(aiValidationService.validateProofImage(any(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Gemini timeout"));

        assertThrows(RuntimeException.class, () ->
                useCase.execute(1L, new byte[]{1, 2, 3}, "photo.jpg", MediaType.PHOTO, null, null, null));
    }

    // ─── Description (text) validation ──────────────────────────────────────────

    @Test
    void execute_returns201_whenDescriptionIsStudyRelated() {
        when(participationRepository.findById(1L)).thenReturn(Optional.of(participation));
        when(aiValidationService.validateEvidenceContent("Li 30 páginas de cálculo"))
                .thenReturn(new EvidenceValidationResult(200, "study_evidence", "Conteúdo educacional", true));
        when(aiValidationService.validateProofImage(any(), anyString(), anyString(), anyString()))
                .thenReturn(VALID_IMAGE);
        when(fileStorageService.store(any(), anyString()))
                .thenReturn(new StoredFile("photo.jpg", "http://cdn/photo.jpg"));
        when(proofRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertNotNull(useCase.execute(1L, new byte[]{1, 2, 3}, "photo.jpg",
                MediaType.PHOTO, null, null, "Li 30 páginas de cálculo"));
    }

    @Test
    void execute_throwsEvidenceValidationException_status400_whenDescriptionIsNotStudyRelated() {
        when(participationRepository.findById(1L)).thenReturn(Optional.of(participation));
        when(aiValidationService.validateEvidenceContent("Assisti um filme ontem"))
                .thenReturn(new EvidenceValidationResult(400, "not_study_related",
                        "Conteúdo sem relação com estudos", false));

        EvidenceValidationException ex = assertThrows(EvidenceValidationException.class, () ->
                useCase.execute(1L, new byte[]{1}, "f.jpg", MediaType.PHOTO,
                        null, null, "Assisti um filme ontem"));

        assertEquals(400, ex.getResult().status());
        assertFalse(ex.getResult().isValid());
    }

    @Test
    void execute_throwsEvidenceValidationException_status450_whenDescriptionIsInappropriate() {
        when(participationRepository.findById(1L)).thenReturn(Optional.of(participation));
        when(aiValidationService.validateEvidenceContent("Odeio todo mundo aqui"))
                .thenReturn(new EvidenceValidationResult(450, "inappropriate_content",
                        "Conteúdo impróprio", false));

        EvidenceValidationException ex = assertThrows(EvidenceValidationException.class, () ->
                useCase.execute(1L, new byte[]{1}, "f.jpg", MediaType.PHOTO,
                        null, null, "Odeio todo mundo aqui"));

        assertEquals(450, ex.getResult().status());
        assertFalse(ex.getResult().isValid());
    }

    // ─── Participation guards ────────────────────────────────────────────────────

    @Test
    void execute_throwsIllegalArgument_whenParticipationNotFound() {
        when(participationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(99L, new byte[]{1}, "f.jpg", MediaType.PHOTO, null, null, null));
    }

    @Test
    void execute_throwsIllegalState_whenParticipationNotAccepted() {
        User user = User.createFromDatabase(2L, "Di", "di@uni.br", "pass123", null);
        ChallengeParticipation invited = ChallengeParticipation.createFromDatabase(
                2L, user, participation.getChallenge(), ParticipationStatus.INVITED, 0);
        when(participationRepository.findById(2L)).thenReturn(Optional.of(invited));

        assertThrows(IllegalStateException.class, () ->
                useCase.execute(2L, new byte[]{1}, "f.jpg", MediaType.PHOTO, null, null, null));
    }
}
