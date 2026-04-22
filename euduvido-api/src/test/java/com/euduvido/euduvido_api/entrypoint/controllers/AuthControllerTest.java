package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.usecases.auth.LoginUseCase;
import com.euduvido.euduvido_api.domain.entities.User;
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

import java.time.Instant;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AuthControllerTest {

    @Autowired WebApplicationContext wac;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean LoginUseCase loginUseCase;

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    void login_returnsOkWithToken_whenCredentialsValid() throws Exception {
        User mockUser = User.createFromDatabase(1L, "Ana", "ana@uni.br", "encoded", null);
        LoginUseCase.Result result = new LoginUseCase.Result(
                "jwt-token-abc", Instant.now().plusSeconds(86400), mockUser);
        when(loginUseCase.execute("ana@uni.br", "senha123")).thenReturn(result);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("email", "ana@uni.br", "password", "senha123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-abc"))
                .andExpect(jsonPath("$.user.email").value("ana@uni.br"));
    }

    @Test
    void login_returns400_whenCredentialsInvalid() throws Exception {
        when(loginUseCase.execute("bad@uni.br", "wrong"))
                .thenThrow(new IllegalArgumentException("Credenciais inválidas"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("email", "bad@uni.br", "password", "wrong"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Credenciais inválidas"));
    }

    @Test
    void login_returns400_whenEmailBlank() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("email", "", "password", "senha123"))))
                .andExpect(status().isBadRequest());
    }
}
