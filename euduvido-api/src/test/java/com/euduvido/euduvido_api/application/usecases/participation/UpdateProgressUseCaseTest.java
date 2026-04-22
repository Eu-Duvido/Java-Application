package com.euduvido.euduvido_api.application.usecases.participation;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.enums.*;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProgressUseCaseTest {

    @Mock ChallengeParticipationRepository participationRepository;

    @InjectMocks UpdateProgressUseCase useCase;

    private ChallengeParticipation participation;

    @BeforeEach
    void setUp() {
        User user = User.createFromDatabase(1L, "Eva", "eva@uni.br", "pass123", null);
        Challenge challenge = Challenge.createFromDatabase(1L, "Exercícios", "Resolver 20 exercícios",
                Difficulty.MEDIUM, "Math", GoalType.EXERCISES, 20,
                user, LocalDateTime.now().plusDays(5),
                ChallengeStatus.ACTIVE, false, LocalDateTime.now(), List.of());
        participation = ChallengeParticipation.createFromDatabase(
                1L, user, challenge, ParticipationStatus.ACCEPTED, 0);
    }

    @Test
    void execute_updatesProgress_whenValid() {
        when(participationRepository.findById(1L)).thenReturn(Optional.of(participation));
        when(participationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ChallengeParticipation result = useCase.execute(1L, 10);

        assertEquals(10, result.getProgress());
    }

    @Test
    void execute_throwsIllegalArgument_whenNotFound() {
        when(participationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(99L, 5));
    }

    @Test
    void execute_throwsIllegalArgument_whenProgressNegative() {
        when(participationRepository.findById(1L)).thenReturn(Optional.of(participation));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(1L, -1));
    }

    @Test
    void execute_throwsIllegalArgument_whenProgressExceedsGoal() {
        when(participationRepository.findById(1L)).thenReturn(Optional.of(participation));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(1L, 21));
    }
}
