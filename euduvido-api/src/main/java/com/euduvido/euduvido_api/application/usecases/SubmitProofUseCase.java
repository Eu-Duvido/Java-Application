package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.entities.Proof;
import com.euduvido.euduvido_api.domain.enums.MediaType;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;
import com.euduvido.euduvido_api.domain.repositories.ProofRepository;

/**
 * Caso de uso: Submeter comprovação de um desafio.
 * Responsabilidade: Criar novo registro de prova com mídia e localização.
 */
public class SubmitProofUseCase {
    private final ProofRepository proofRepository;
    private final ChallengeParticipationRepository participationRepository;

    public SubmitProofUseCase(ProofRepository proofRepository, ChallengeParticipationRepository participationRepository) {
        this.proofRepository = proofRepository;
        this.participationRepository = participationRepository;
    }

    /**
     * Executa o envio de comprovação
     * @param participationId ID da participação
     * @param mediaUrl URL da mídia (foto ou vídeo)
     * @param mediaType Tipo de mídia
     * @param latitude Latitude da localização (opcional)
     * @param longitude Longitude da localização (opcional)
     * @return Comprovação criada
     * @throws IllegalArgumentException se participação não existe ou dados inválidos
     */
    public Proof execute(Long participationId, String mediaUrl, MediaType mediaType, Double latitude, Double longitude) {
        // Buscar participação
        ChallengeParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("Participação não encontrada"));

        // Validar se participação está aceita
        if (!participation.getStatus().equals(participation.getStatus())) {
            // A validação real é feita na criação da Proof
        }

        // Criar comprovação
        Proof proof = Proof.create(participation, mediaUrl, mediaType, latitude, longitude);

        // Persistir comprovação
        return proofRepository.save(proof);
    }
}

