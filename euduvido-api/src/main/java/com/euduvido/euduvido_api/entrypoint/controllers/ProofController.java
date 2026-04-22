package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.usecases.proof.ApproveProofUseCase;
import com.euduvido.euduvido_api.application.usecases.proof.RejectProofUseCase;
import com.euduvido.euduvido_api.entrypoint.dtos.request.RejectProofRequest;
import com.euduvido.euduvido_api.entrypoint.dtos.response.ProofResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/proofs")
@Tag(name = "Comprovações", description = "Aprovação e rejeição de comprovações enviadas")
public class ProofController {
    private final ApproveProofUseCase approveProofUseCase;
    private final RejectProofUseCase rejectProofUseCase;

    public ProofController(ApproveProofUseCase approveProofUseCase, RejectProofUseCase rejectProofUseCase) {
        this.approveProofUseCase = approveProofUseCase;
        this.rejectProofUseCase = rejectProofUseCase;
    }

    @Operation(summary = "Aprovar comprovação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comprovação aprovada"),
            @ApiResponse(responseCode = "409", description = "Comprovação já foi avaliada")
    })
    @PostMapping("/{id}/approve")
    public ResponseEntity<ProofResponse> approveProof(@PathVariable Long id) {
        return ResponseEntity.ok(ProofResponse.fromDomain(approveProofUseCase.execute(id)));
    }

    @Operation(summary = "Rejeitar comprovação", description = "Motivo da rejeição é opcional")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comprovação rejeitada"),
            @ApiResponse(responseCode = "409", description = "Comprovação já foi avaliada")
    })
    @PostMapping("/{id}/reject")
    public ResponseEntity<ProofResponse> rejectProof(
            @PathVariable Long id,
            @RequestBody(required = false) RejectProofRequest request) {
        String reason = request != null ? request.getReason() : null;
        return ResponseEntity.ok(ProofResponse.fromDomain(rejectProofUseCase.execute(id, reason)));
    }
}
