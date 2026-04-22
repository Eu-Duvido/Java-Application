package com.euduvido.euduvido_api.application.usecases.challenge;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class UpdateExpiredChallengesUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateExpiredChallengesUseCase.class);

    private final ChallengeRepository challengeRepository;

    public UpdateExpiredChallengesUseCase(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    /** Expira todos os desafios cujo deadline já passou e ainda estão PENDING ou ACTIVE. */
    public List<Challenge> execute() {
        List<Challenge> candidates = challengeRepository.findExpiredCandidates(
                LocalDateTime.now(),
                List.of(ChallengeStatus.PENDING, ChallengeStatus.ACTIVE));

        List<Challenge> expired = candidates.stream()
                .peek(Challenge::expire)
                .map(challengeRepository::save)
                .toList();

        if (!expired.isEmpty()) {
            log.info("[ExpireChallenges] {} desafio(s) expirado(s): {}",
                    expired.size(),
                    expired.stream().map(c -> "#" + c.getId() + " " + c.getTitle()).toList());
        }

        return expired;
    }
}
