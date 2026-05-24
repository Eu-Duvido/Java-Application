package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.usecases.challenge.AcceptChallengeUseCase;
import com.euduvido.euduvido_api.application.usecases.challenge.ListReceivedChallengesUseCase;
import com.euduvido.euduvido_api.application.usecases.challenge.RefuseChallengeUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.*;
import com.euduvido.euduvido_api.application.usecases.proof.ApproveProofUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.SubmitProofUseCase;
import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.enums.*;
import com.euduvido.euduvido_api.domain.pagination.PageResult;
import com.euduvido.euduvido_api.infrastructure.security.AuthUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ParticipationControllerTest {

    @Autowired WebApplicationContext wac;

    @MockitoBean AcceptChallengeUseCase acceptChallengeUseCase;
    @MockitoBean RefuseChallengeUseCase refuseChallengeUseCase;
    @MockitoBean SubmitProofUseCase submitProofUseCase;
    @MockitoBean ApproveProofUseCase approveProofUseCase;
    @MockitoBean ListReceivedChallengesUseCase listReceivedChallengesUseCase;
    @MockitoBean ListSentInvitesUseCase listSentInvitesUseCase;
    @MockitoBean CreateChallengeParticipationUseCase createChallengeParticipationUseCase;
    @MockitoBean DeleteChallengeParticipationUseCase deleteChallengeParticipationUseCase;
    @MockitoBean UpdateChallengeParticipationUseCase updateChallengeParticipationUseCase;
    @MockitoBean UpdateProgressUseCase updateProgressUseCase;

    MockMvc mockMvc;
    AuthUser authUser;
    ChallengeParticipation participation;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
        User user = User.createFromDatabase(1L, "Gio", "gio@uni.br", "encoded", null);
        authUser = new AuthUser(user);
        Challenge challenge = Challenge.createFromDatabase(1L, "Desafio", "Desc",
                Difficulty.EASY, "Math", GoalType.HOURS, 2,
                user, LocalDateTime.now().plusDays(5),
                ChallengeStatus.ACTIVE, false, LocalDateTime.now(), List.of());
        participation = ChallengeParticipation.createFromDatabase(
                10L, user, challenge, ParticipationStatus.INVITED, 0);
    }

    @Test
    void acceptChallenge_returns200() throws Exception {
        ChallengeParticipation accepted = ChallengeParticipation.createFromDatabase(
                10L, participation.getUser(), participation.getChallenge(),
                ParticipationStatus.ACCEPTED, 0);
        when(acceptChallengeUseCase.execute(10L)).thenReturn(accepted);

        mockMvc.perform(post("/api/v1/participations/10/accept")
                        .with(user(authUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void refuseChallenge_returns200() throws Exception {
        ChallengeParticipation refused = ChallengeParticipation.createFromDatabase(
                10L, participation.getUser(), participation.getChallenge(),
                ParticipationStatus.REFUSED, 0);
        when(refuseChallengeUseCase.execute(10L)).thenReturn(refused);

        mockMvc.perform(post("/api/v1/participations/10/refuse")
                        .with(user(authUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUSED"));
    }

    @Test
    void listReceived_returns200WithPage() throws Exception {
        PageResult<ChallengeParticipation> page =
                new PageResult<>(List.of(participation), 1L, 1, 0, 20);
        when(listReceivedChallengesUseCase.execute(any(), any(), anyInt(), anyInt()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/participations/received")
                        .with(user(authUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void acceptChallenge_returns403_whenUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/participations/10/accept"))
                .andExpect(status().isForbidden());
    }
}
