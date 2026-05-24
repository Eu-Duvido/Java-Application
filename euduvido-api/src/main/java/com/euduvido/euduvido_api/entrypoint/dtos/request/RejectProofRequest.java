package com.euduvido.euduvido_api.entrypoint.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectProofRequest {
    private String reason;
}
