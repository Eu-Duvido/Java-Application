package com.euduvido.euduvido_api.infrastructure.persistence.entities;

import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.enums.MediaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade JPA que mapeia a tabela de comprovações de desafios.
 * Representa o mapeamento técnico da entidade de domínio Proof.
 */
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

    @Column(name = "approved")
    private Boolean approved;

    /**
     * Converte entidade JPA para entidade de domínio
     */
    public Proof toDomain() {
        return Proof.createFromDatabase(
                id,
                participation.toDomain(),
                mediaUrl,
                mediaType,
                latitude,
                longitude,
                submittedAt,
                approved
        );
    }

    /**
     * Cria entidade JPA a partir de entidade de domínio
     */
    public static ProofEntity fromDomain(Proof proof) {
        ProofEntity entity = new ProofEntity();
        entity.setId(proof.getId());
        entity.setParticipation(ChallengeParticipationEntity.fromDomain(proof.getParticipation()));
        entity.setMediaUrl(proof.getMediaUrl());
        entity.setMediaType(proof.getMediaType());
        entity.setLatitude(proof.getLatitude());
        entity.setLongitude(proof.getLongitude());
        entity.setSubmittedAt(proof.getSubmittedAt());
        entity.setApproved(proof.getApproved());
        return entity;
    }
}

