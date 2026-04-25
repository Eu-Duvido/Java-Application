package com.euduvido.euduvido_api.application.usecases.challenge;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.domain.pagination.PageResult;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;

import java.util.Optional;

public class ListChallengeUseCase {
    private final ChallengeRepository challengeRepository;

    public ListChallengeUseCase(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    public PageResult<Challenge> execute(Optional<ChallengeStatus> status, Optional<String> title, int page, int size) {
        return challengeRepository.findAllPaged(status, title, page, size);
    }
}
