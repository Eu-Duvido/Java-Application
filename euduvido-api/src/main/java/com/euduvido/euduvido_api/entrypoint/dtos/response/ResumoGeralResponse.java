package com.euduvido.euduvido_api.entrypoint.dtos.response;

public record ResumoGeralResponse(
        Integer nuAnoCenso,
        Long totalIes,
        Long totalCursos,
        Long totalVagas,
        Long totalIngressantes,
        Long totalMatriculados,
        Long totalConcluintes,
        Double pctIngFeminino,
        Double pctIngMasculino,
        Double taxaConclusaoPct,
        Long totalIngViaEnem,
        Double pctEnem
) {}
