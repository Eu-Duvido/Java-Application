package com.euduvido.euduvido_api.infrastructure.persistence.entities;

import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.enums.MediaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "proofs")
public class ProofEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id", nullable = false)
    private ChallengeParticipationEntity participation;

    @Column(name = "media_url", nullable = false)
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @Column(name = "approved", nullable = false)
    private boolean approved;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "ai_valid")
    private Boolean aiValid;

    @Column(name = "ai_confidence")
    private Double aiConfidence;

    @Column(name = "ai_reason", columnDefinition = "TEXT")
    private String aiReason;

    public Proof toDomain() {
        return Proof.createFromDatabase(id, participation.toDomain(), mediaUrl, mediaType,
                latitude, longitude, submittedAt, approved, rejectionReason, aiValid, aiConfidence, aiReason);
    }

    public static ProofEntity fromDomain(Proof proof) {
        ProofEntity entity = new ProofEntity();
        entity.setId(proof.getId());
        entity.setParticipation(ChallengeParticipationEntity.fromDomain(proof.getParticipation()));
        entity.setMediaUrl(proof.getMediaUrl());
        entity.setMediaType(proof.getMediaType());
        entity.setLatitude(proof.getLatitude());
        entity.setLongitude(proof.getLongitude());
        entity.setSubmittedAt(proof.getSubmittedAt());
        entity.setApproved(proof.isApproved());
        entity.setRejectionReason(proof.getRejectionReason());
        entity.setAiValid(proof.getAiValid());
        entity.setAiConfidence(proof.getAiConfidence());
        entity.setAiReason(proof.getAiReason());
        return entity;
    }
}
