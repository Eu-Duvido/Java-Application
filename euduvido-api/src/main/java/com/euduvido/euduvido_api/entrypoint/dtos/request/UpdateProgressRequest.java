package com.euduvido.euduvido_api.entrypoint.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProgressRequest {
    @NotNull(message = "Progresso é obrigatório")
    @Min(value = 0, message = "Progresso não pode ser negativo")
    private Integer progress;
}
