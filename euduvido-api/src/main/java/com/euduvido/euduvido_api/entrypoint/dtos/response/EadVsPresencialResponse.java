package com.euduvido.euduvido_api.entrypoint.dtos.response;

public record EadVsPresencialResponse(
        String modalidade,
        Long totalIngressantes,
        Long totalMatriculados,
        Long totalConcluintes,
        Long totalVagas,
        Long numCursos
) {}
