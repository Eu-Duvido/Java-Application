package com.euduvido.euduvido_api.application.usecases.proof;

import com.euduvido.euduvido_api.application.exception.EvidenceValidationException;
import com.euduvido.euduvido_api.application.services.AiValidationService;
import com.euduvido.euduvido_api.application.services.EvidenceValidationResult;
import com.euduvido.euduvido_api.application.services.FileStorageService;
import com.euduvido.euduvido_api.application.services.StoredFile;
import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.enums.MediaType;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;
import com.euduvido.euduvido_api.domain.repositories.ProofRepository;

/**
 * Caso de uso: Submeter comprovação de um desafio via upload de arquivo.
 */
public class SubmitProofUseCase {

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
                         MediaType mediaType, Double latitude, Double longitude, String description) {
        ChallengeParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("Participação não encontrada"));

        if (participation.getStatus() != ParticipationStatus.ACCEPTED) {
            throw new IllegalStateException("Só é possível enviar comprovação em participações aceitas");
        }

        // Valida o texto descritivo antes de qualquer operação de I/O
        // Conteúdo impróprio (450) bloqueia o envio; não relacionado a estudos (400) salva com aiValid=false
        if (description != null && !description.isBlank()) {
            EvidenceValidationResult textResult = aiValidationService.validateEvidenceContent(description);
            if (textResult.status() != 200) {
                throw new EvidenceValidationException(textResult);
            }
        }

        // Valida a imagem/vídeo ANTES de armazenar o arquivo
        // Conteúdo impróprio (450) bloqueia o envio; não relacionado a estudos (400) salva com aiValid=false
        EvidenceValidationResult imageResult = null;
        if (mediaType == MediaType.PHOTO || mediaType == MediaType.VIDEO) {
            String mimeType = resolveMimeType(originalFilename, mediaType);
            imageResult = aiValidationService.validateProofImage(
                    fileBytes, mimeType,
                    participation.getChallenge().getTitle(),
                    participation.getChallenge().getDescription());
            if (imageResult.status() != 200) {
                throw new EvidenceValidationException(imageResult);
            }
        }

        StoredFile stored = fileStorageService.store(fileBytes, originalFilename);
        Proof proof = Proof.create(participation, stored.url(), mediaType, latitude, longitude);

        if (imageResult != null) {
            proof.setAiValid(imageResult.isValid());
            proof.setAiReason(imageResult.message());
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
