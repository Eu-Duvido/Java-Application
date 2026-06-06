package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.usecases.InviteUserToChallengeUseCase;
import com.euduvido.euduvido_api.application.usecases.challenge.*;
import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.entrypoint.dtos.request.CreateChallengeRequest;
import com.euduvido.euduvido_api.entrypoint.dtos.request.UpdateChallengeRequest;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.entrypoint.dtos.response.ChallengeParticipationResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.ChallengeResponse;
import com.euduvido.euduvido_api.entrypoint.dtos.response.PageResponse;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/challenges")
@Tag(name = "Desafios", description = "Criação, listagem e gerenciamento de desafios acadêmicos")
public class ChallengeController {
    private final CreateChallengeUseCase createChallengeUseCase;
    private final GetChallengeByIdUseCase getChallengeByIdUseCase;
    private final UpdateChallengeUseCase updateChallengeUseCase;
    private final DeleteChallengeUseCase deleteChallengeUseCase;
    private final ListChallengeUseCase listChallengeUseCase;
    private final ListCreatedChallengesUseCase listCreatedChallengesUseCase;
    private final InviteUserToChallengeUseCase inviteUserUseCase;

    public ChallengeController(CreateChallengeUseCase createChallengeUseCase,
                               GetChallengeByIdUseCase getChallengeByIdUseCase,
                               UpdateChallengeUseCase updateChallengeUseCase,
                               DeleteChallengeUseCase deleteChallengeUseCase,
                               ListChallengeUseCase listChallengeUseCase,
                               ListCreatedChallengesUseCase listCreatedChallengesUseCase,
                               InviteUserToChallengeUseCase inviteUserUseCase) {
        this.createChallengeUseCase = createChallengeUseCase;
        this.getChallengeByIdUseCase = getChallengeByIdUseCase;
        this.updateChallengeUseCase = updateChallengeUseCase;
        this.deleteChallengeUseCase = deleteChallengeUseCase;
        this.listChallengeUseCase = listChallengeUseCase;
        this.listCreatedChallengesUseCase = listCreatedChallengesUseCase;
        this.inviteUserUseCase = inviteUserUseCase;
    }

    @Operation(summary = "Criar desafio", description = "Cria novo desafio; conteúdo validado por IA (retorna 422 se rejeitado)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Desafio criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "422", description = "Conteúdo rejeitado pela validação IA")
    })
    @PostMapping
    public ResponseEntity<ChallengeResponse> createChallenge(
            @AuthenticationPrincipal AuthUser principal,
            @Valid @RequestBody CreateChallengeRequest request) {
        Challenge challenge = createChallengeUseCase.execute(
                principal.getId(), request.getTitle(), request.getDescription(),
                request.getDeadline(), request.getLocationRequired(), request.getDifficulty(),
                request.getSubject(), request.getGoalType(), request.getGoalValue());
        return ResponseEntity.status(HttpStatus.CREATED).body(ChallengeResponse.fromDomain(challenge));
    }

    @Operation(summary = "Listar desafios", description = "Retorna desafios paginados, opcionalmente filtrados por status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    @GetMapping
    public ResponseEntity<PageResponse<ChallengeResponse>> listChallenges(
            @RequestParam(required = false) ChallengeStatus status,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = listChallengeUseCase.execute(
                Optional.ofNullable(status), Optional.ofNullable(title), page, size);
        return ResponseEntity.ok(PageResponse.fromDomain(result, ChallengeResponse::fromDomain));
    }

    @Operation(summary = "Buscar desafio por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Desafio encontrado"),
            @ApiResponse(responseCode = "404", description = "Desafio não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeResponse> getChallengeById(@PathVariable Long id) {
        return ResponseEntity.ok(ChallengeResponse.fromDomain(getChallengeByIdUseCase.execute(id)));
    }

    @Operation(summary = "Atualizar desafio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Desafio atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Usuário não é o criador"),
            @ApiResponse(responseCode = "404", description = "Desafio não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ChallengeResponse> updateChallenge(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthUser principal,
            @Valid @RequestBody UpdateChallengeRequest request) {
        Challenge challenge = updateChallengeUseCase.execute(
                id, principal.getId(), request.getTitle(), request.getDescription(),
                request.getDeadline(), request.getLocationRequired(), request.getDifficulty(),
                request.getSubject(), request.getGoalType(), request.getGoalValue());
        return ResponseEntity.ok(ChallengeResponse.fromDomain(challenge));
    }

    @Operation(summary = "Deletar desafio")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Desafio removido"),
            @ApiResponse(responseCode = "404", description = "Desafio não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        deleteChallengeUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar desafios criados pelo usuário")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<ChallengeResponse>> listCreatedChallenges(@PathVariable Long creatorId) {
        return ResponseEntity.ok(listCreatedChallengesUseCase.execute(creatorId)
                .stream().map(ChallengeResponse::fromDomain).toList());
    }

    @Operation(summary = "Convidar usuário autenticado para desafio")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Convite criado"),
            @ApiResponse(responseCode = "409", description = "Usuário já participa do desafio")
    })
    @PostMapping("/{id}/invite")
    public ResponseEntity<ChallengeParticipationResponse> inviteUserToChallenge(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthUser principal) {
        var participation = inviteUserUseCase.execute(principal.getId(), id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ChallengeParticipationResponse.fromDomain(participation));
    }
}
