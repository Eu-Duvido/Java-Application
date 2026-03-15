package com.euduvido.euduvido_api.domain.entities;

import com.euduvido.euduvido_api.domain.enums.MediaType;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<User> approvers;

    public Proof() {
    }

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

    public static Proof create(ChallengeParticipation participation, String mediaUrl, MediaType mediaType,
                               Double latitude, Double longitude) {
        validateProofData(participation, mediaUrl, mediaType);
        return new Proof(null, participation, mediaUrl, mediaType, latitude, longitude, LocalDateTime.now(), false);
    }

    public static Proof createFromDatabase(Long id, ChallengeParticipation participation, String mediaUrl,
                                           MediaType mediaType, Double latitude, Double longitude,
                                           LocalDateTime submittedAt, Boolean approved) {
        return new Proof(id, participation, mediaUrl, mediaType, latitude, longitude, submittedAt, approved);
    }

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

    public void approve() {
        if (this.approved) {
            throw new IllegalStateException("Comprovação já foi aprovada");
        }
        this.approved = true;
        // Ao aprovar, marcar participação como completada
        this.participation.complete();
    }

    public void reject() {
        if (this.approved) {
            throw new IllegalStateException("Comprovação já foi aprovada e não pode ser rejeitada");
        }
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public ChallengeParticipation getParticipation() {
        return participation;
    }
    public void setParticipation(ChallengeParticipation participation) {
        this.participation = participation;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }
    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public MediaType getMediaType() {
        return mediaType;
    }
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Boolean getApproved() {
        return approved;
    }
    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public List<User> getApprovers() {
        return approvers;
    }
    public void setApprovers(List<User> approvers) {
        this.approvers = approvers;
    }
}

