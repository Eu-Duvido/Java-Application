package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.usecases.*;
import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.entrypoint.dtos.request.CreateChallengeRequest;
import com.euduvido.euduvido_api.entrypoint.dtos.response.ChallengeResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciar desafios.
 * Responsabilidade: Receber requisições HTTP e delegar para casos de uso.
 */
@RestController
@RequestMapping("/api/v1/challenges")
public class ChallengeController {
    private final CreateChallengeUseCase createChallengeUseCase;
    private final InviteUserToChallengeUseCase inviteUserUseCase;
    private final ListCreatedChallengesUseCase listCreatedChallengesUseCase;
    private final DeleteChallengeUseCase deleteChallengeUseCase;
    private final ListChallengeUseCase listChallengeUseCase;
    private final UpdateChallengeUseCase updateChallengeUseCase;

    public ChallengeController(CreateChallengeUseCase createChallengeUseCase,
                              InviteUserToChallengeUseCase inviteUserUseCase,
                              ListCreatedChallengesUseCase listCreatedChallengesUseCase,
                               DeleteChallengeUseCase deleteChallengeUseCase,
                               ListChallengeUseCase listChallengeUseCase,
                               UpdateChallengeUseCase updateChallengeUseCase) {
        this.createChallengeUseCase = createChallengeUseCase;
        this.inviteUserUseCase = inviteUserUseCase;
        this.listCreatedChallengesUseCase = listCreatedChallengesUseCase;
        this.deleteChallengeUseCase = deleteChallengeUseCase;
        this.listChallengeUseCase = listChallengeUseCase;
        this.updateChallengeUseCase = updateChallengeUseCase;
    }

    /**
     * POST /api/v1/challenges
     * Criar um novo desafio
     */
    @PostMapping
    public ResponseEntity<ChallengeResponse> createChallenge(
            @RequestParam Long creatorId,
            @Valid @RequestBody CreateChallengeRequest request) {
        var challenge = createChallengeUseCase.execute(
                creatorId,
                request.getTitle(),
                request.getDescription(),
                request.getDeadline(),
                request.getLocationRequired()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ChallengeResponse.fromDomain(challenge));
    }

    /**
     * GET /api/v1/challenges/{id}
     * Obter detalhes de um desafio (implementado com busca na listagem do criador)
     */
    @GetMapping("/{id}")
    public ResponseEntity<String> getChallengeDetails(@PathVariable Long id) {
        return ResponseEntity.ok("Endpoint para detalhe do desafio " + id);
    }

    /**
     * POST /api/v1/challenges/{id}/invite
     * Convidar um usuário para um desafio
     */
    @PostMapping("/{id}/invite")
    public ResponseEntity<String> inviteUserToChallenge(
            @PathVariable Long id,
            @RequestParam Long userId) {
        inviteUserUseCase.execute(userId, id);
        return ResponseEntity.ok("Usuário convidado com sucesso");
    }

    /**
     * GET /api/v1/challenges/creator/{creatorId}
     * Listar desafios criados por um usuário
     */
    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<ChallengeResponse>> listCreatedChallenges(@PathVariable Long creatorId) {
        var challenges = listCreatedChallengesUseCase.execute(creatorId);
        var responses = challenges.stream()
                .map(ChallengeResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * DELETE /api/v1/challenges/{id}
     * Deletar desafio por id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        deleteChallengeUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/challenges/
     * Listar todos os desafios
     */
    @GetMapping("/{id}")
    public ResponseEntity<List<ChallengeResponse>> listChallenges() {
        List<Challenge> challengesDomain = listChallengeUseCase.execute();

        return ResponseEntity.ok().body(challengesDomain.
                stream()
                .map(ChallengeResponse::fromDomain)
                .toList());
    }

    /**
     * PUT /api/v1/challenges/
     * Listar todos os desafios
     */
    @PutMapping()
    public ResponseEntity<ChallengeResponse> updateChallenge(@RequestParam Long creatorId,
                                                             @Valid @RequestBody CreateChallengeRequest request) {
        Challenge challengeDomain = updateChallengeUseCase.execute(
                creatorId,
                request.getTitle(),
                request.getDescription(),
                request.getDeadline(),
                request.getLocationRequired()
        );

        return ResponseEntity.ok().body(ChallengeResponse.fromDomain(challengeDomain));
    }
}

