package com.euduvido.euduvido_api.entrypoint.dtos.request;

import com.euduvido.euduvido_api.domain.enums.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de request para submissão de comprovação.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitProofRequest {
    @NotBlank(message = "URL da mídia é obrigatória")
    private String mediaUrl;

    @NotNull(message = "Tipo de mídia é obrigatório")
    private MediaType mediaType;

    private Double latitude;

    private Double longitude;
}

