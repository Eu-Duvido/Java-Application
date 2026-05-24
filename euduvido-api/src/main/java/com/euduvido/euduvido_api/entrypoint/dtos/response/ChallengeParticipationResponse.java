package com.euduvido.euduvido_api.entrypoint.dtos.response;

import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeParticipationResponse {
    private Long id;
    private UserResponse user;
    private ChallengeResponse challenge;
    private ParticipationStatus status;
    private Integer progress;

    public static ChallengeParticipationResponse fromDomain(ChallengeParticipation participation) {
        return new ChallengeParticipationResponse(
                participation.getId(),
                UserResponse.fromDomain(participation.getUser()),
                ChallengeResponse.fromDomain(participation.getChallenge()),
                participation.getStatus(),
                participation.getProgress());
    }
}
