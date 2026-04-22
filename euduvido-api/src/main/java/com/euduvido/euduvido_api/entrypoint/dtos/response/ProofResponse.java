package com.euduvido.euduvido_api.entrypoint.dtos.response;

import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private boolean approved;
    private String rejectionReason;
    private Boolean aiValid;
    private Double aiConfidence;
    private String aiReason;

    public static ProofResponse fromDomain(Proof proof) {
        ProofResponse r = new ProofResponse();
        r.setId(proof.getId());
        r.setParticipationId(proof.getParticipation().getId());
        r.setMediaUrl(proof.getMediaUrl());
        r.setMediaType(proof.getMediaType());
        r.setLatitude(proof.getLatitude());
        r.setLongitude(proof.getLongitude());
        r.setSubmittedAt(proof.getSubmittedAt());
        r.setApproved(proof.isApproved());
        r.setRejectionReason(proof.getRejectionReason());
        r.setAiValid(proof.getAiValid());
        r.setAiConfidence(proof.getAiConfidence());
        r.setAiReason(proof.getAiReason());
        return r;
    }
}
