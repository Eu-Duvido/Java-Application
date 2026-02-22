package com.euduvido.euduvido_api.application.usecases.proof;

import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.enums.MediaType;
import com.euduvido.euduvido_api.domain.repositories.ProofRepository;

/**
 * Caso de uso: Atualizar uma comprovação.
 */
public class UpdateProofUseCase {
    private final ProofRepository proofRepository;

    public UpdateProofUseCase(ProofRepository proofRepository) {
        this.proofRepository = proofRepository;
    }

    /**
     * Executa a atualização de uma comprovação
     * @param id ID da comprovação
     * @param mediaUrl nova URL da mídia (opcional)
     * @param mediaType novo tipo de mídia (opcional)
     * @param latitude nova latitude (opcional)
     * @param longitude nova longitude (opcional)
     * @return Comprovação atualizada
     */
    public Proof execute(Long id, String mediaUrl, MediaType mediaType, Double latitude, Double longitude) {
        Proof existing = proofRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comprovação não encontrada"));

        if (Boolean.TRUE.equals(existing.getApproved())) {
            throw new IllegalStateException("Comprovação já foi aprovada e não pode ser alterada");
        }

        if (mediaUrl != null && !mediaUrl.trim().isEmpty()) {
            existing.setMediaUrl(mediaUrl);
        }
        if (mediaType != null) {
            existing.setMediaType(mediaType);
        }
        if (latitude != null) {
            existing.setLatitude(latitude);
        }
        if (longitude != null) {
            existing.setLongitude(longitude);
        }

        return proofRepository.save(existing);
    }
}

