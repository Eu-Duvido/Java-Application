package com.euduvido.euduvido_api.entrypoint.dtos.response;

public record EvidenceMetricsResponse(
        Long approvedCount,
        Long rejectedCount,
        Long pendingCount,
        Long totalCount,
        Double approvalRate
) {}
