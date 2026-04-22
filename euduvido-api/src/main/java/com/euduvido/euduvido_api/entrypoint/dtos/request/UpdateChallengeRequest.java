package com.euduvido.euduvido_api.entrypoint.dtos.request;

import com.euduvido.euduvido_api.domain.enums.Difficulty;
import com.euduvido.euduvido_api.domain.enums.GoalType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChallengeRequest {
    private String title;
    private String description;

    @Future(message = "Deadline deve ser no futuro")
    private LocalDateTime deadline;

    private Boolean locationRequired;
    private Difficulty difficulty;
    private String subject;
    private GoalType goalType;

    @Min(value = 1, message = "Valor da meta deve ser maior que zero")
    private Integer goalValue;
}
