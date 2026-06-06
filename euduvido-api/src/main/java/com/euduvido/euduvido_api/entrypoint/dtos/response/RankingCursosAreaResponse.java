package com.euduvido.euduvido_api.entrypoint.dtos.response;

public record RankingCursosAreaResponse(
        String noAreaGeral,
        Long totalIngressantes,
        Long totalMatriculados,
        Long totalConcluintes,
        Long numCursos,
        Double pctConclusao
) {}
