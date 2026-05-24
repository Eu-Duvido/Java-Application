package com.euduvido.euduvido_api.entrypoint.dtos.request;

import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de request para atualização de participação em um desafio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateParticipationRequest {
    @NotNull(message = "ID da participação é obrigatório")
    private Long idChallengeParticipation;

    @NotNull(message = "ID do usuário é obrigatório")
    private Long userId;

    @NotNull(message = "ID do desafio é obrigatório")
    private Long challengeId;

    @NotNull(message = "Status é obrigatório")
    private ParticipationStatus status;
}
