package com.euduvido.euduvido_api.entrypoint.dtos.response;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.domain.enums.Difficulty;
import com.euduvido.euduvido_api.domain.enums.GoalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponse {
    private Long id;
    private String title;
    private String description;
    private Difficulty difficulty;
    private String subject;
    private GoalType goalType;
    private Integer goalValue;
    private UserResponse creator;
    private LocalDateTime deadline;
    private ChallengeStatus status;
    private Boolean locationRequired;
    private LocalDateTime createdAt;

    public static ChallengeResponse fromDomain(Challenge challenge) {
        return new ChallengeResponse(
                challenge.getId(),
                challenge.getTitle(),
                challenge.getDescription(),
                challenge.getDifficulty(),
                challenge.getSubject(),
                challenge.getGoalType(),
                challenge.getGoalValue(),
                UserResponse.fromDomain(challenge.getCreator()),
                challenge.getDeadline(),
                challenge.getStatus(),
                challenge.getLocationRequired(),
                challenge.getCreatedAt());
    }
}
