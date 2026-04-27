package com.euduvido.euduvido_api.application.usecases.proof;

import com.euduvido.euduvido_api.application.services.AiValidationService;
import com.euduvido.euduvido_api.application.services.FileStorageService;
import com.euduvido.euduvido_api.application.services.StoredFile;
import com.euduvido.euduvido_api.application.services.ValidationResult;
import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.enums.MediaType;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;
import com.euduvido.euduvido_api.domain.repositories.ProofRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Caso de uso: Submeter comprovação de um desafio via upload de arquivo.
 */
public class SubmitProofUseCase {
    private static final Logger log = LoggerFactory.getLogger(SubmitProofUseCase.class);

    private final ProofRepository proofRepository;
    private final ChallengeParticipationRepository participationRepository;
    private final FileStorageService fileStorageService;
    private final AiValidationService aiValidationService;

    public SubmitProofUseCase(ProofRepository proofRepository,
                               ChallengeParticipationRepository participationRepository,
                               FileStorageService fileStorageService,
                               AiValidationService aiValidationService) {
        this.proofRepository = proofRepository;
        this.participationRepository = participationRepository;
        this.fileStorageService = fileStorageService;
        this.aiValidationService = aiValidationService;
    }

    public Proof execute(Long participationId, byte[] fileBytes, String originalFilename,
                         MediaType mediaType, Double latitude, Double longitude) {
        ChallengeParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("Participação não encontrada"));

        if (participation.getStatus() != ParticipationStatus.ACCEPTED) {
            throw new IllegalStateException("Só é possível enviar comprovação em participações aceitas");
        }

        StoredFile stored = fileStorageService.store(fileBytes, originalFilename);
        Proof proof = Proof.create(participation, stored.url(), mediaType, latitude, longitude);

        if (mediaType == MediaType.PHOTO || mediaType == MediaType.VIDEO) {
            try {
                String mimeType = resolveMimeType(originalFilename, mediaType);
                String challengeTitle = participation.getChallenge().getTitle();
                String challengeDescription = participation.getChallenge().getDescription();
                ValidationResult result = aiValidationService.validateProofImage(
                        fileBytes, mimeType, challengeTitle, challengeDescription);
                proof.setAiValid(result.valid());
                proof.setAiConfidence(result.confidence());
                proof.setAiReason(result.reason());
            } catch (Exception e) {
                log.warn("Validação IA da prova falhou, seguindo sem resultado: {}", e.getMessage());
            }
        }

        return proofRepository.save(proof);
    }

    private String resolveMimeType(String filename, MediaType mediaType) {
        if (filename != null) {
            String lower = filename.toLowerCase();
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
            if (lower.endsWith(".png")) return "image/png";
            if (lower.endsWith(".gif")) return "image/gif";
            if (lower.endsWith(".webp")) return "image/webp";
        }
        return mediaType == MediaType.VIDEO ? "video/mp4" : "image/jpeg";
    }
}
