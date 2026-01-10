package com.euduvido.euduvido_api.entrypoint.dtos.response;

import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de response para comprovação.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProofResponse {
    private Long id;
    private Long participationId;
    private String mediaUrl;
    private MediaType mediaType;
    private Double latitude;
    private Double longitude;
    private LocalDateTime submittedAt;
    private Boolean approved;

    /**
     * Cria DTO a partir da entidade de domínio
     */
    public static ProofResponse fromDomain(Proof proof) {
        return new ProofResponse(
                proof.getId(),
                proof.getParticipation().getId(),
                proof.getMediaUrl(),
                proof.getMediaType(),
                proof.getLatitude(),
                proof.getLongitude(),
                proof.getSubmittedAt(),
                proof.getApproved()
        );
    }
}

