package com.euduvido.euduvido_api.application.usecases.challenge;

import com.euduvido.euduvido_api.application.exception.AiValidationException;
import com.euduvido.euduvido_api.application.services.AiValidationService;
import com.euduvido.euduvido_api.application.services.ValidationResult;
import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.domain.enums.Difficulty;
import com.euduvido.euduvido_api.domain.enums.GoalType;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;
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
class CreateChallengeUseCaseTest {

    @Mock ChallengeRepository challengeRepository;
    @Mock UserRepository userRepository;
    @Mock AiValidationService aiValidationService;

    @InjectMocks CreateChallengeUseCase useCase;

    private User creator;
    private final ValidationResult valid = new ValidationResult(true, 0.95, "OK", List.of());
    private final ValidationResult invalid = new ValidationResult(false, 0.2, "Conteúdo inapropriado", List.of("issue1"));

    @BeforeEach
    void setUp() {
        creator = User.createFromDatabase(1L, "Ana", "ana@uni.br", "pass123", null);
    }

    @Test
    void execute_createsChallenge_whenValidAndAiApproves() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(aiValidationService.validateChallenge(anyString(), anyString(), anyString())).thenReturn(valid);
        Challenge saved = Challenge.createFromDatabase(1L, "Ler 30 páginas", "Leitura",
                Difficulty.EASY, "Portuguese", GoalType.PAGES, 30,
                creator, LocalDateTime.now().plusDays(5), ChallengeStatus.PENDING,
                false, LocalDateTime.now(), List.of());
        when(challengeRepository.save(any())).thenReturn(saved);

        Challenge result = useCase.execute(1L, "Ler 30 páginas", "Leitura",
                LocalDateTime.now().plusDays(5), false, Difficulty.EASY, "Portuguese",
                GoalType.PAGES, 30);

        assertNotNull(result);
        assertEquals("Ler 30 páginas", result.getTitle());
        assertEquals(ChallengeStatus.PENDING, result.getStatus());
    }

    @Test
    void execute_throwsIllegalArgument_whenCreatorNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(99L, "Título", "Desc",
                        LocalDateTime.now().plusDays(5), false, Difficulty.EASY,
                        "Math", GoalType.HOURS, 2));
    }

    @Test
    void execute_throwsAiValidationException_whenAiRejects() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(aiValidationService.validateChallenge(anyString(), anyString(), anyString())).thenReturn(invalid);

        assertThrows(AiValidationException.class, () ->
                useCase.execute(1L, "Título", "Desc",
                        LocalDateTime.now().plusDays(5), false, Difficulty.EASY,
                        "Math", GoalType.HOURS, 2));
    }
}
