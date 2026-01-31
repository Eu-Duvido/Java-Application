package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.usecases.*;
import com.euduvido.euduvido_api.entrypoint.dtos.request.CreateParticipationRequest;
import com.euduvido.euduvido_api.entrypoint.dtos.request.SubmitProofRequest;
import com.euduvido.euduvido_api.entrypoint.dtos.response.ChallengeParticipationResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.ProofResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciar participações em desafios.
 * Responsabilidade: Receber requisições HTTP e delegar para casos de uso.
 */
@RestController
@RequestMapping("/api/v1/participations")
public class ParticipationController {
    private final AcceptChallengeUseCase acceptChallengeUseCase;
    private final RefuseChallengeUseCase refuseChallengeUseCase;
    private final SubmitProofUseCase submitProofUseCase;
    private final ApproveProofUseCase approveProofUseCase;
    private final ListReceivedChallengesUseCase listReceivedChallengesUseCase;
    private final CreateChallengeParticipationUseCase createChallengeParticipationUseCase;
    private final DeleteChallengeParticipationUseCase deleteChallengeParticipationUseCase;

    public ParticipationController(AcceptChallengeUseCase acceptChallengeUseCase,
                                   RefuseChallengeUseCase refuseChallengeUseCase,
                                   SubmitProofUseCase submitProofUseCase,
                                   ApproveProofUseCase approveProofUseCase,
                                   ListReceivedChallengesUseCase listReceivedChallengesUseCase,
                                   CreateChallengeParticipationUseCase createChallengeParticipationUseCase,
                                   DeleteChallengeParticipationUseCase deleteChallengeParticipationUseCase) {
        this.acceptChallengeUseCase = acceptChallengeUseCase;
        this.refuseChallengeUseCase = refuseChallengeUseCase;
        this.submitProofUseCase = submitProofUseCase;
        this.approveProofUseCase = approveProofUseCase;
        this.listReceivedChallengesUseCase = listReceivedChallengesUseCase;
        this.createChallengeParticipationUseCase = createChallengeParticipationUseCase;
        this.deleteChallengeParticipationUseCase = deleteChallengeParticipationUseCase;
    }

    /**
     * POST /api/v1/participations/{id}/accept
     * Aceitar um convite de desafio
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<ChallengeParticipationResponse> acceptChallenge(@PathVariable Long id) {
        var participation = acceptChallengeUseCase.execute(id);
        return ResponseEntity.ok(ChallengeParticipationResponse.fromDomain(participation));
    }

    /**
     * POST /api/v1/participations/{id}/refuse
     * Recusar um convite de desafio
     */
    @PostMapping("/{id}/refuse")
    public ResponseEntity<ChallengeParticipationResponse> refuseChallenge(@PathVariable Long id) {
        var participation = refuseChallengeUseCase.execute(id);
        return ResponseEntity.ok(ChallengeParticipationResponse.fromDomain(participation));
    }

    /**
     * POST /api/v1/participations/{id}/proof
     * Submeter comprovação de um desafio
     */
    @PostMapping("/{id}/proof")
    public ResponseEntity<ProofResponse> submitProof(
            @PathVariable Long id,
            @Valid @RequestBody SubmitProofRequest request) {
        var proof = submitProofUseCase.execute(
                id,
                request.getMediaUrl(),
                request.getMediaType(),
                request.getLatitude(),
                request.getLongitude()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProofResponse.fromDomain(proof));
    }

    /**
     * GET /api/v1/participations/user/{userId}
     * Listar desafios recebidos por um usuário
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChallengeParticipationResponse>> listReceivedChallenges(@PathVariable Long userId) {
        var participations = listReceivedChallengesUseCase.execute(userId);
        var responses = participations.stream()
                .map(ChallengeParticipationResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * POST /api/v1/participations
     * Criar a participação em um desafio
     */
    @PostMapping
    public ResponseEntity<ChallengeParticipationResponse> createParticipationChallenge(@Valid @RequestBody CreateParticipationRequest request) {
        var participaiton = createChallengeParticipationUseCase.execute(request.getUser(), request.getChallenge());

        return ResponseEntity.status(HttpStatus.CREATED).body(ChallengeParticipationResponse.fromDomain(participaiton));
    }

    /**
     * DELETE /api/v1/participations/{id}
     * Deletar a participação em um desafio
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipationChallenge(@PathVariable Long id) {
        deleteChallengeParticipationUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }
}

