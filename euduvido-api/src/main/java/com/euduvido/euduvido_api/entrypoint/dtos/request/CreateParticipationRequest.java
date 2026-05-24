package com.euduvido.euduvido_api.entrypoint.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de request para criação de participação em um desafio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateParticipationRequest {
    @NotNull(message = "ID do usuário é obrigatório")
    private Long userId;

    @NotNull(message = "ID do desafio é obrigatório")
    private Long challengeId;
}
