package com.euduvido.euduvido_api.domain.entities;

import com.euduvido.euduvido_api.domain.enums.MediaType;

import java.time.LocalDateTime;

/**
 * Entidade de domínio que representa a comprovação de um desafio.
 * Armazena a mídia (foto/vídeo) e localização da prova.
 */
public class Proof {
    private Long id;
    private ChallengeParticipation participation;
    private String mediaUrl;
    private MediaType mediaType;
    private Double latitude;
    private Double longitude;
    private LocalDateTime submittedAt;
    private Boolean approved;

    private Proof(Long id, ChallengeParticipation participation, String mediaUrl, MediaType mediaType,
                  Double latitude, Double longitude, LocalDateTime submittedAt, Boolean approved) {
        this.id = id;
        this.participation = participation;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.submittedAt = submittedAt;
        this.approved = approved;
    }

    // Factory method para criar uma nova comprovação
    public static Proof create(ChallengeParticipation participation, String mediaUrl, MediaType mediaType,
                               Double latitude, Double longitude) {
        validateProofData(participation, mediaUrl, mediaType);
        return new Proof(null, participation, mediaUrl, mediaType, latitude, longitude, LocalDateTime.now(), false);
    }

    // Factory method para recriar comprovação do banco de dados
    public static Proof createFromDatabase(Long id, ChallengeParticipation participation, String mediaUrl,
                                           MediaType mediaType, Double latitude, Double longitude,
                                           LocalDateTime submittedAt, Boolean approved) {
        return new Proof(id, participation, mediaUrl, mediaType, latitude, longitude, submittedAt, approved);
    }

    // Validações de domínio
    private static void validateProofData(ChallengeParticipation participation, String mediaUrl, MediaType mediaType) {
        if (participation == null) {
            throw new IllegalArgumentException("Comprovação deve estar associada a uma participação");
        }
        if (mediaUrl == null || mediaUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("URL da mídia não pode ser vazia");
        }
        if (mediaType == null) {
            throw new IllegalArgumentException("Tipo de mídia deve ser especificado");
        }
    }

    // Lógica de negócio: aprovar comprovação
    public void approve() {
        if (this.approved) {
            throw new IllegalStateException("Comprovação já foi aprovada");
        }
        this.approved = true;
        // Ao aprovar, marcar participação como completada
        this.participation.complete();
    }

    // Lógica de negócio: rejeitar comprovação
    public void reject() {
        if (this.approved) {
            throw new IllegalStateException("Comprovação já foi aprovada e não pode ser rejeitada");
        }
        // A comprovação é simplesmente não aprovada
    }

    // Getters
    public Long getId() {
        return id;
    }

    public ChallengeParticipation getParticipation() {
        return participation;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public Boolean getApproved() {
        return approved;
    }
}

