package com.euduvido.euduvido_api.infrastructure.scheduling;

import com.euduvido.euduvido_api.application.usecases.challenge.UpdateExpiredChallengesUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpireChallengesJob {

    private static final Logger log = LoggerFactory.getLogger(ExpireChallengesJob.class);

    private final UpdateExpiredChallengesUseCase updateExpiredChallengesUseCase;

    public ExpireChallengesJob(UpdateExpiredChallengesUseCase updateExpiredChallengesUseCase) {
        this.updateExpiredChallengesUseCase = updateExpiredChallengesUseCase;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void run() {
        log.debug("[ExpireChallengesJob] Verificando desafios expirados...");
        updateExpiredChallengesUseCase.execute();
    }
}
