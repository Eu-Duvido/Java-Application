package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.exception.AiValidationException;
import com.euduvido.euduvido_api.application.services.ValidationResult;
import com.euduvido.euduvido_api.application.usecases.InviteUserToChallengeUseCase;
import com.euduvido.euduvido_api.application.usecases.challenge.*;
import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.domain.enums.Difficulty;
import com.euduvido.euduvido_api.domain.enums.GoalType;
import com.euduvido.euduvido_api.domain.pagination.PageResult;
import com.euduvido.euduvido_api.infrastructure.security.AuthUser;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ChallengeControllerTest {

    @Autowired WebApplicationContext wac;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean CreateChallengeUseCase createChallengeUseCase;
    @MockitoBean GetChallengeByIdUseCase getChallengeByIdUseCase;
    @MockitoBean UpdateChallengeUseCase updateChallengeUseCase;
    @MockitoBean DeleteChallengeUseCase deleteChallengeUseCase;
    @MockitoBean ListChallengeUseCase listChallengeUseCase;
    @MockitoBean ListCreatedChallengesUseCase listCreatedChallengesUseCase;
    @MockitoBean InviteUserToChallengeUseCase inviteUserToChallengeUseCase;

    MockMvc mockMvc;
    AuthUser authUser;
    Challenge sampleChallenge;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
        User creator = User.createFromDatabase(1L, "Bia", "bia@uni.br", "encoded", null);
        authUser = new AuthUser(creator);
        sampleChallenge = Challenge.createFromDatabase(1L, "Ler 10 páginas", "Leitura",
                Difficulty.EASY, "Lit", GoalType.PAGES, 10,
                creator, LocalDateTime.now().plusDays(7),
                ChallengeStatus.PENDING, false, LocalDateTime.now(), List.of());
    }

    @Test
    void listChallenges_returns200WithPage() throws Exception {
        PageResult<Challenge> page = new PageResult<>(List.of(sampleChallenge), 1L, 1, 0, 20);
        when(listChallengeUseCase.execute(any(), anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/v1/challenges")
                        .with(user(authUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Ler 10 páginas"));
    }

    @Test
    void getChallengeById_returns200() throws Exception {
        when(getChallengeByIdUseCase.execute(1L)).thenReturn(sampleChallenge);

        mockMvc.perform(get("/api/v1/challenges/1")
                        .with(user(authUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createChallenge_returns201_whenValid() throws Exception {
        when(createChallengeUseCase.execute(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(sampleChallenge);

        Map<String, Object> body = Map.of(
                "title", "Ler 10 páginas",
                "description", "Leitura",
                "deadline", LocalDateTime.now().plusDays(7).toString(),
                "locationRequired", false,
                "difficulty", "EASY",
                "subject", "Lit",
                "goalType", "PAGES",
                "goalValue", 10);

        mockMvc.perform(post("/api/v1/challenges")
                        .with(user(authUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Ler 10 páginas"));
    }

    @Test
    void deleteChallenge_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/challenges/1")
                        .with(user(authUser)))
                .andExpect(status().isNoContent());
    }

    @Test
    void listChallenges_returns403_whenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/challenges"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createChallenge_returns422_whenAiRejects() throws Exception {
        when(createChallengeUseCase.execute(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new AiValidationException(
                        new ValidationResult(false, 0.1, "Conteúdo inapropriado", List.of("issue"))));

        Map<String, Object> body = Map.of(
                "title", "Desafio ruim",
                "description", "Desc",
                "deadline", LocalDateTime.now().plusDays(7).toString(),
                "locationRequired", false,
                "difficulty", "EASY",
                "subject", "Any",
                "goalType", "HOURS",
                "goalValue", 1);

        mockMvc.perform(post("/api/v1/challenges")
                        .with(user(authUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnprocessableEntity());
    }
}
