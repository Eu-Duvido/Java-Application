package com.euduvido.euduvido_api.entrypoint.dtos.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de request para criação de desafio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChallengeRequest {
    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    @NotNull(message = "Deadline é obrigatório")
    @Future(message = "Deadline deve ser no futuro")
    private LocalDateTime deadline;

    private Boolean locationRequired;
}

