package com.euduvido.euduvido_api.domain.entities;

import com.euduvido.euduvido_api.domain.enums.MediaType;

import java.time.LocalDateTime;
import java.util.List;

public class Proof {
    private Long id;
    private ChallengeParticipation participation;
    private String mediaUrl;
    private MediaType mediaType;
    private Double latitude;
    private Double longitude;
    private LocalDateTime submittedAt;
    private boolean approved;
    private String rejectionReason;
    private List<User> approvers;
    private Boolean aiValid;
    private Double aiConfidence;
    private String aiReason;

    public Proof() {}

    private Proof(Long id, ChallengeParticipation participation, String mediaUrl, MediaType mediaType,
                  Double latitude, Double longitude, LocalDateTime submittedAt, boolean approved,
                  String rejectionReason, Boolean aiValid, Double aiConfidence, String aiReason) {
        this.id = id;
        this.participation = participation;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.submittedAt = submittedAt;
        this.approved = approved;
        this.rejectionReason = rejectionReason;
        this.aiValid = aiValid;
        this.aiConfidence = aiConfidence;
        this.aiReason = aiReason;
    }

    public static Proof create(ChallengeParticipation participation, String mediaUrl, MediaType mediaType,
                               Double latitude, Double longitude) {
        if (participation == null) throw new IllegalArgumentException("Comprovação deve estar associada a uma participação");
        if (mediaUrl == null || mediaUrl.trim().isEmpty()) throw new IllegalArgumentException("URL da mídia não pode ser vazia");
        if (mediaType == null) throw new IllegalArgumentException("Tipo de mídia deve ser especificado");
        return new Proof(null, participation, mediaUrl, mediaType, latitude, longitude, LocalDateTime.now(), false, null, null, null, null);
    }

    public static Proof createFromDatabase(Long id, ChallengeParticipation participation, String mediaUrl,
                                           MediaType mediaType, Double latitude, Double longitude,
                                           LocalDateTime submittedAt, boolean approved, String rejectionReason,
                                           Boolean aiValid, Double aiConfidence, String aiReason) {
        return new Proof(id, participation, mediaUrl, mediaType, latitude, longitude, submittedAt, approved, rejectionReason, aiValid, aiConfidence, aiReason);
    }

    public void approve() {
        if (this.approved) throw new IllegalStateException("Comprovação já foi aprovada");
        this.approved = true;
        this.rejectionReason = null;
        this.participation.complete();
    }

    public void reject(String reason) {
        if (this.approved) throw new IllegalStateException("Comprovação já foi aprovada e não pode ser rejeitada");
        this.rejectionReason = reason;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ChallengeParticipation getParticipation() { return participation; }
    public void setParticipation(ChallengeParticipation participation) { this.participation = participation; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public MediaType getMediaType() { return mediaType; }
    public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public List<User> getApprovers() { return approvers; }
    public void setApprovers(List<User> approvers) { this.approvers = approvers; }
    public Boolean getAiValid() { return aiValid; }
    public void setAiValid(Boolean aiValid) { this.aiValid = aiValid; }
    public Double getAiConfidence() { return aiConfidence; }
    public void setAiConfidence(Double aiConfidence) { this.aiConfidence = aiConfidence; }
    public String getAiReason() { return aiReason; }
    public void setAiReason(String aiReason) { this.aiReason = aiReason; }
}
