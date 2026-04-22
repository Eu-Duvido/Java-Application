package com.euduvido.euduvido_api.entrypoint.dtos.request;

import com.euduvido.euduvido_api.domain.enums.Difficulty;
import com.euduvido.euduvido_api.domain.enums.GoalType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @NotNull(message = "Dificuldade é obrigatória")
    private Difficulty difficulty;

    private String subject;

    @NotNull(message = "Tipo de meta é obrigatório")
    private GoalType goalType;

    @NotNull(message = "Valor da meta é obrigatório")
    @Min(value = 1, message = "Valor da meta deve ser maior que zero")
    private Integer goalValue;
}
