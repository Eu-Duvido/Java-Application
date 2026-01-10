package com.euduvido.euduvido_api.entrypoint.dtos.response;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de response para desafio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponse {
    private Long id;
    private String title;
    private String description;
    private UserResponse creator;
    private LocalDateTime deadline;
    private ChallengeStatus status;
    private Boolean locationRequired;
    private LocalDateTime createdAt;

    /**
     * Cria DTO a partir da entidade de domínio
     */
    public static ChallengeResponse fromDomain(Challenge challenge) {
        return new ChallengeResponse(
                challenge.getId(),
                challenge.getTitle(),
                challenge.getDescription(),
                UserResponse.fromDomain(challenge.getCreator()),
                challenge.getDeadline(),
                challenge.getStatus(),
                challenge.getLocationRequired(),
                challenge.getCreatedAt()
        );
    }
}

