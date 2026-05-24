package com.euduvido.euduvido_api.application.usecases.challenge;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.domain.enums.Difficulty;
import com.euduvido.euduvido_api.domain.enums.GoalType;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateExpiredChallengesUseCaseTest {

    @Mock ChallengeRepository challengeRepository;

    @InjectMocks UpdateExpiredChallengesUseCase useCase;

    private User creator;

    @BeforeEach
    void setUp() {
        creator = User.createFromDatabase(1L, "Fred", "fred@uni.br", "pass123", null);
    }

    private Challenge expiredChallenge(Long id, ChallengeStatus status) {
        return Challenge.createFromDatabase(id, "Desafio " + id, "Desc",
                Difficulty.EASY, "Any", GoalType.HOURS, 1,
                creator, LocalDateTime.now().minusHours(1),
                status, false, LocalDateTime.now().minusDays(1), List.of());
    }

    @Test
    void execute_expiresAllPendingAndActiveCandidates() {
        Challenge pending = expiredChallenge(1L, ChallengeStatus.PENDING);
        Challenge active  = expiredChallenge(2L, ChallengeStatus.ACTIVE);
        when(challengeRepository.findExpiredCandidates(any(), any()))
                .thenReturn(List.of(pending, active));
        when(challengeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        List<Challenge> result = useCase.execute();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> c.getStatus() == ChallengeStatus.EXPIRED));
    }

    @Test
    void execute_returnsEmptyList_whenNoCandidates() {
        when(challengeRepository.findExpiredCandidates(any(), any())).thenReturn(List.of());

        List<Challenge> result = useCase.execute();

        assertTrue(result.isEmpty());
    }

    @Test
    void execute_throwsWhenTryingToExpireCompleted() {
        Challenge completed = Challenge.createFromDatabase(3L, "Done", "Desc",
                Difficulty.EASY, "Any", GoalType.HOURS, 1,
                creator, LocalDateTime.now().minusHours(1),
                ChallengeStatus.COMPLETED, false, LocalDateTime.now().minusDays(1), List.of());
        when(challengeRepository.findExpiredCandidates(any(), any()))
                .thenReturn(List.of(completed));

        assertThrows(IllegalStateException.class, () -> useCase.execute());
    }
}
