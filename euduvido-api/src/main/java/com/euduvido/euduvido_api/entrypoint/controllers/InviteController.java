package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.usecases.AcceptInviteUseCase;
import com.euduvido.euduvido_api.application.usecases.CreateInviteUseCase;
import com.euduvido.euduvido_api.application.usecases.DeleteInviteUseCase;
import com.euduvido.euduvido_api.application.usecases.GetInviteUseCase;
import com.euduvido.euduvido_api.application.usecases.ListInvitesUseCase;
import com.euduvido.euduvido_api.entrypoint.dtos.request.CreateInviteRequest;
import com.euduvido.euduvido_api.entrypoint.dtos.response.InviteResponse;
import com.euduvido.euduvido_api.domain.entities.Invite;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invites")
public class InviteController {
    private final CreateInviteUseCase createInviteUseCase;
    private final GetInviteUseCase getInviteUseCase;
    private final ListInvitesUseCase listInvitesUseCase;
    private final DeleteInviteUseCase deleteInviteUseCase;
    private final AcceptInviteUseCase acceptInviteUseCase;

    public InviteController(CreateInviteUseCase createInviteUseCase,
                            GetInviteUseCase getInviteUseCase,
                            ListInvitesUseCase listInvitesUseCase,
                            DeleteInviteUseCase deleteInviteUseCase,
                            AcceptInviteUseCase acceptInviteUseCase) {
        this.createInviteUseCase = createInviteUseCase;
        this.getInviteUseCase = getInviteUseCase;
        this.listInvitesUseCase = listInvitesUseCase;
        this.deleteInviteUseCase = deleteInviteUseCase;
        this.acceptInviteUseCase = acceptInviteUseCase;
    }

    @PostMapping
    public ResponseEntity<InviteResponse> createInvite(@Valid @RequestBody CreateInviteRequest request) {
        Invite invite = createInviteUseCase.execute(request.getSenderId(), request.getRecipientId(), request.getMessage());
        return ResponseEntity.status(HttpStatus.CREATED).body(InviteResponse.fromDomain(invite));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InviteResponse> getInvite(@PathVariable Long id) {
        return getInviteUseCase.execute(id)
                .map(inv -> ResponseEntity.ok(InviteResponse.fromDomain(inv)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<InviteResponse>> listBySender(@PathVariable Long senderId) {
        List<InviteResponse> list = listInvitesUseCase.listBySender(senderId).stream()
                .map(InviteResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<List<InviteResponse>> listByRecipient(@PathVariable Long recipientId) {
        List<InviteResponse> list = listInvitesUseCase.listByRecipient(recipientId).stream()
                .map(InviteResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvite(@PathVariable Long id) {
        deleteInviteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<InviteResponse> acceptInvite(@PathVariable Long id) {
        Invite invited = acceptInviteUseCase.execute(id);
        return ResponseEntity.ok(InviteResponse.fromDomain(invited));
    }
}
