package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.usecases.challenge.AcceptChallengeUseCase;
import com.euduvido.euduvido_api.application.usecases.challenge.ListReceivedChallengesUseCase;
import com.euduvido.euduvido_api.application.usecases.challenge.RefuseChallengeUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.CreateChallengeParticipationUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.DeleteChallengeParticipationUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.ListChallengeParticipationUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.ListSentInvitesUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.SelfJoinChallengeUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.UpdateChallengeParticipationUseCase;
import com.euduvido.euduvido_api.application.usecases.participation.UpdateProgressUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.ApproveProofUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.ListProofsByChallengeUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.ListProofsByParticipationUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.SubmitProofUseCase;
import com.euduvido.euduvido_api.domain.enums.MediaType;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import com.euduvido.euduvido_api.entrypoint.dtos.request.CreateParticipationRequest;
import com.euduvido.euduvido_api.entrypoint.dtos.request.UpdateParticipationRequest;
import com.euduvido.euduvido_api.entrypoint.dtos.request.UpdateProgressRequest;
import com.euduvido.euduvido_api.entrypoint.dtos.response.ChallengeParticipationResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.PageResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.ProofResponse;
import com.euduvido.euduvido_api.infrastructure.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/participations")
@Tag(name = "Participações", description = "Aceitação, recusa e progresso em desafios")
public class ParticipationController {
    private final AcceptChallengeUseCase acceptChallengeUseCase;
    private final RefuseChallengeUseCase refuseChallengeUseCase;
    private final SubmitProofUseCase submitProofUseCase;
    private final ApproveProofUseCase approveProofUseCase;
    private final ListProofsByChallengeUseCase listProofsByChallengeUseCase;
    private final ListReceivedChallengesUseCase listReceivedChallengesUseCase;
    private final ListSentInvitesUseCase listSentInvitesUseCase;
    private final CreateChallengeParticipationUseCase createChallengeParticipationUseCase;
    private final DeleteChallengeParticipationUseCase deleteChallengeParticipationUseCase;
    private final UpdateChallengeParticipationUseCase updateChallengeParticipationUseCase;
    private final UpdateProgressUseCase updateProgressUseCase;
    private final ListProofsByParticipationUseCase listProofsByParticipationUseCase;
    private final ListProofsByChallengeUseCase listProofsByChallengeUseCase;
    private final SelfJoinChallengeUseCase selfJoinChallengeUseCase;
    private final ListChallengeParticipationUseCase listChallengeParticipationUseCase;

    public ParticipationController(AcceptChallengeUseCase acceptChallengeUseCase,
                                   RefuseChallengeUseCase refuseChallengeUseCase,
                                   SubmitProofUseCase submitProofUseCase,
                                   ApproveProofUseCase approveProofUseCase,
                                   ListProofsByChallengeUseCase listProofsByChallengeUseCase,
                                   ListReceivedChallengesUseCase listReceivedChallengesUseCase,
                                   ListSentInvitesUseCase listSentInvitesUseCase,
                                   CreateChallengeParticipationUseCase createChallengeParticipationUseCase,
                                   DeleteChallengeParticipationUseCase deleteChallengeParticipationUseCase,
                                   UpdateChallengeParticipationUseCase updateChallengeParticipationUseCase,
                                   UpdateProgressUseCase updateProgressUseCase,
                                   ListProofsByParticipationUseCase listProofsByParticipationUseCase,
                                   ListProofsByChallengeUseCase listProofsByChallengeUseCase,
                                   SelfJoinChallengeUseCase selfJoinChallengeUseCase,
                                   ListChallengeParticipationUseCase listChallengeParticipationUseCase) {
        this.acceptChallengeUseCase = acceptChallengeUseCase;
        this.refuseChallengeUseCase = refuseChallengeUseCase;
        this.submitProofUseCase = submitProofUseCase;
        this.approveProofUseCase = approveProofUseCase;
        this.listProofsByChallengeUseCase = listProofsByChallengeUseCase;
        this.listReceivedChallengesUseCase = listReceivedChallengesUseCase;
        this.listSentInvitesUseCase = listSentInvitesUseCase;
        this.createChallengeParticipationUseCase = createChallengeParticipationUseCase;
        this.deleteChallengeParticipationUseCase = deleteChallengeParticipationUseCase;
        this.updateChallengeParticipationUseCase = updateChallengeParticipationUseCase;
        this.updateProgressUseCase = updateProgressUseCase;
        this.selfJoinChallengeUseCase = selfJoinChallengeUseCase;
    }

    @Operation(summary = "Listar comprovações de todos os participantes de um desafio")
    @ApiResponse(responseCode = "200", description = "Lista de comprovações retornada")
    @GetMapping("/challenge/{challengeId}/proofs")
    public ResponseEntity<List<ProofResponse>> listProofsByChallenge(@PathVariable Long challengeId) {
        var proofs = listProofsByChallengeUseCase.execute(challengeId)
                .stream()
                .map(ProofResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(proofs);
        this.listProofsByParticipationUseCase = listProofsByParticipationUseCase;
        this.listProofsByChallengeUseCase = listProofsByChallengeUseCase;
        this.selfJoinChallengeUseCase = selfJoinChallengeUseCase;
        this.listChallengeParticipationUseCase = listChallengeParticipationUseCase;
    }

    @Operation(summary = "Aceitar desafio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado para ACCEPTED"),
            @ApiResponse(responseCode = "409", description = "Transição de estado inválida")
    })
    @PostMapping("/{id}/accept")
    public ResponseEntity<ChallengeParticipationResponse> acceptChallenge(@PathVariable Long id) {
        return ResponseEntity.ok(ChallengeParticipationResponse.fromDomain(acceptChallengeUseCase.execute(id)));
    }

    @Operation(summary = "Recusar desafio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado para REFUSED"),
            @ApiResponse(responseCode = "409", description = "Transição de estado inválida")
    })
    @PostMapping("/{id}/refuse")
    public ResponseEntity<ChallengeParticipationResponse> refuseChallenge(@PathVariable Long id) {
        return ResponseEntity.ok(ChallengeParticipationResponse.fromDomain(refuseChallengeUseCase.execute(id)));
    }

    @Operation(summary = "Enviar comprovação",
            description = "Upload multipart de foto/vídeo. O campo 'description' opcional é validado por IA: retorna 400 se não for conteúdo de estudo, 450 se for impróprio.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Comprovação enviada"),
            @ApiResponse(responseCode = "400", description = "Conteúdo sem relação com estudos"),
            @ApiResponse(responseCode = "409", description = "Participação não está em ACCEPTED"),
            @ApiResponse(responseCode = "450", description = "Conteúdo impróprio ou inadequado para a plataforma")
    })
    @PostMapping(value = "/{id}/proof", consumes = "multipart/form-data")
    public ResponseEntity<ProofResponse> submitProof(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            @RequestParam MediaType mediaType,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) String description) throws IOException {
        var proof = submitProofUseCase.execute(
                id, file.getBytes(), file.getOriginalFilename(), mediaType, latitude, longitude, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProofResponse.fromDomain(proof));
    }

    @Operation(summary = "Atualizar progresso da participação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progresso atualizado"),
            @ApiResponse(responseCode = "400", description = "Valor de progresso inválido"),
            @ApiResponse(responseCode = "409", description = "Participação não está em ACCEPTED")
    })
    @PatchMapping("/{id}/progress")
    public ResponseEntity<ChallengeParticipationResponse> updateProgress(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProgressRequest request) {
        var participation = updateProgressUseCase.execute(id, request.getProgress());
        return ResponseEntity.ok(ChallengeParticipationResponse.fromDomain(participation));
    }

    @Operation(summary = "Listar participações de um usuário")
    @ApiResponse(responseCode = "200", description = "Lista paginada retornada")
    @GetMapping("/user/{userId}")
    public ResponseEntity<PageResponse<ChallengeParticipationResponse>> listByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) ParticipationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = listReceivedChallengesUseCase.execute(userId, Optional.ofNullable(status), page, size);
        return ResponseEntity.ok(PageResponse.fromDomain(result, ChallengeParticipationResponse::fromDomain));
    }

    @Operation(summary = "Listar convites recebidos pelo usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Convites com status INVITED")
    @GetMapping("/received")
    public ResponseEntity<PageResponse<ChallengeParticipationResponse>> listReceived(
            @AuthenticationPrincipal AuthUser principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = listReceivedChallengesUseCase.execute(
                principal.getId(), Optional.of(ParticipationStatus.INVITED), page, size);
        return ResponseEntity.ok(PageResponse.fromDomain(result, ChallengeParticipationResponse::fromDomain));
    }

    @Operation(summary = "Listar convites enviados pelo criador autenticado")
    @ApiResponse(responseCode = "200", description = "Convites com status INVITED")
    @GetMapping("/sent")
    public ResponseEntity<PageResponse<ChallengeParticipationResponse>> listSent(
            @AuthenticationPrincipal AuthUser principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = listSentInvitesUseCase.execute(principal.getId(), page, size);
        return ResponseEntity.ok(PageResponse.fromDomain(result, ChallengeParticipationResponse::fromDomain));
    }

    @Operation(summary = "Criador entra no próprio desafio",
            description = "Cria ou retorna uma participação ACCEPTED para o usuário autenticado no desafio informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Participação retornada ou criada"),
            @ApiResponse(responseCode = "404", description = "Desafio não encontrado")
    })
    @PostMapping("/self-join/{challengeId}")
    public ResponseEntity<ChallengeParticipationResponse> selfJoin(
            @PathVariable Long challengeId,
            @AuthenticationPrincipal AuthUser principal) {
        var participation = selfJoinChallengeUseCase.execute(principal.getId(), challengeId);
        return ResponseEntity.ok(ChallengeParticipationResponse.fromDomain(participation));
    }

    @Operation(summary = "Criar participação diretamente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Participação criada"),
            @ApiResponse(responseCode = "409", description = "Usuário já participa do desafio")
    })
    @PostMapping
    public ResponseEntity<ChallengeParticipationResponse> createParticipationChallenge(
            @Valid @RequestBody CreateParticipationRequest request) {
        var participation = createChallengeParticipationUseCase.execute(request.getUserId(), request.getChallengeId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ChallengeParticipationResponse.fromDomain(participation));
    }

    @Operation(summary = "Remover participação")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Participação removida"),
            @ApiResponse(responseCode = "404", description = "Participação não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipationChallenge(@PathVariable Long id) {
        deleteChallengeParticipationUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar participação (admin)")
    @ApiResponse(responseCode = "200", description = "Participação atualizada")
    @PutMapping
    public ResponseEntity<ChallengeParticipationResponse> updateParticipationChallenge(
            @Valid @RequestBody UpdateParticipationRequest request) {
        var updated = updateChallengeParticipationUseCase.execute(
                request.getIdChallengeParticipation(), request.getUserId(),
                request.getChallengeId(), request.getStatus());
        return ResponseEntity.ok(ChallengeParticipationResponse.fromDomain(updated));
    }

    @Operation(summary = "Listar participantes de um desafio, ordenados por progresso decrescente")
    @ApiResponse(responseCode = "200", description = "Lista de participantes retornada")
    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<List<ChallengeParticipationResponse>> listByChallenge(@PathVariable Long challengeId) {
        var participations = new java.util.ArrayList<>(
                listChallengeParticipationUseCase.executeByChallengeId(challengeId));
        participations.sort((a, b) -> Integer.compare(
                b.getProgress() == null ? 0 : b.getProgress(),
                a.getProgress() == null ? 0 : a.getProgress()));
        return ResponseEntity.ok(participations.stream()
                .map(ChallengeParticipationResponse::fromDomain)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Criador entra no próprio desafio como participante ACCEPTED")
    @ApiResponse(responseCode = "201", description = "Participação criada ou retornada existente")
    @PostMapping("/self-join/{challengeId}")
    public ResponseEntity<ChallengeParticipationResponse> selfJoin(
            @AuthenticationPrincipal AuthUser principal,
            @PathVariable Long challengeId) {
        var participation = selfJoinChallengeUseCase.execute(principal.getId(), challengeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ChallengeParticipationResponse.fromDomain(participation));
    }

    @Operation(summary = "Listar comprovações de uma participação")
    @ApiResponse(responseCode = "200", description = "Lista de comprovações retornada")
    @GetMapping("/{id}/proofs")
    public ResponseEntity<List<ProofResponse>> listProofsByParticipation(@PathVariable Long id) {
        var proofs = listProofsByParticipationUseCase.execute(id);
        return ResponseEntity.ok(proofs.stream().map(ProofResponse::fromDomain).collect(Collectors.toList()));
    }

    @Operation(summary = "Listar todas as comprovações de um desafio (todas as participações)")
    @ApiResponse(responseCode = "200", description = "Lista de comprovações retornada, ordenada por data desc")
    @GetMapping("/challenge/{challengeId}/proofs")
    public ResponseEntity<List<ProofResponse>> listProofsByChallenge(@PathVariable Long challengeId) {
        var proofs = listProofsByChallengeUseCase.execute(challengeId);
        return ResponseEntity.ok(proofs.stream().map(ProofResponse::fromDomain).collect(Collectors.toList()));
    }
}
