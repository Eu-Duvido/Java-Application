package com.euduvido.euduvido_api.application.usecases.proof;

import com.euduvido.euduvido_api.application.services.AiValidationService;
import com.euduvido.euduvido_api.application.services.FileStorageService;
import com.euduvido.euduvido_api.application.services.StoredFile;
import com.euduvido.euduvido_api.application.services.ValidationResult;
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

    @Test
    void execute_savesProofWithAiFields_whenImageUploadAndAiSucceeds() {
        when(participationRepository.findById(1L)).thenReturn(Optional.of(participation));
        when(fileStorageService.store(any(), anyString()))
                .thenReturn(new StoredFile("photo.jpg", "http://cdn/photo.jpg"));
        when(aiValidationService.validateProofImage(any(), anyString(), anyString(), anyString()))
                .thenReturn(new ValidationResult(true, 0.88, "Comprovação válida", List.of()));
        when(proofRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Proof result = useCase.execute(1L, new byte[]{1, 2, 3}, "photo.jpg",
                MediaType.PHOTO, null, null);

        assertNotNull(result);
        assertEquals("http://cdn/photo.jpg", result.getMediaUrl());
        assertTrue(result.getAiValid());
        assertEquals(0.88, result.getAiConfidence());
    }

    @Test
    void execute_throwsIllegalArgument_whenParticipationNotFound() {
        when(participationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(99L, new byte[]{1}, "f.jpg", MediaType.PHOTO, null, null));
    }

    @Test
    void execute_throwsIllegalState_whenParticipationNotAccepted() {
        User user = User.createFromDatabase(2L, "Di", "di@uni.br", "pass123", null);
        Challenge challenge = participation.getChallenge();
        ChallengeParticipation invited = ChallengeParticipation.createFromDatabase(
                2L, user, challenge, ParticipationStatus.INVITED, 0);
        when(participationRepository.findById(2L)).thenReturn(Optional.of(invited));

        assertThrows(IllegalStateException.class, () ->
                useCase.execute(2L, new byte[]{1}, "f.jpg", MediaType.PHOTO, null, null));
    }

    @Test
    void execute_savesProofEvenWhenAiFails() {
        when(participationRepository.findById(1L)).thenReturn(Optional.of(participation));
        when(fileStorageService.store(any(), anyString()))
                .thenReturn(new StoredFile("photo.jpg", "http://cdn/photo.jpg"));
        when(aiValidationService.validateProofImage(any(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("AI timeout"));
        when(proofRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Proof result = useCase.execute(1L, new byte[]{1, 2, 3}, "photo.jpg",
                MediaType.PHOTO, null, null);

        assertNotNull(result);
        assertNull(result.getAiValid());
    }
}
