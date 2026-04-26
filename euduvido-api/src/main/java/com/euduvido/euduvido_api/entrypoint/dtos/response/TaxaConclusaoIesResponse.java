package com.euduvido.euduvido_api.entrypoint.dtos.response;

public record TaxaConclusaoIesResponse(
        String noIes,
        String sgIes,
        String noRegiao,
        String sgUf,
        Long totalMatriculados,
        Long totalConcluintes,
        Double taxaConclusaoPct,
        Double taxaEvasaoPct
) {}
