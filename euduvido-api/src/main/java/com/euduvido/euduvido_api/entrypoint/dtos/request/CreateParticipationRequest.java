package com.euduvido.euduvido_api.entrypoint.dtos.request;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.User;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Usuário é obrigatório")
    private User user;

    @NotBlank(message = "Desafio é obrigatório")
    private Challenge challenge;
}
