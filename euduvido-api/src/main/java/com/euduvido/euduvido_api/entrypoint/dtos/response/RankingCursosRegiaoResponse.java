package com.euduvido.euduvido_api.entrypoint.dtos.response;

public record RankingCursosRegiaoResponse(
        String noRegiao,
        String noCurso,
        String noAreaGeral,
        Long totalIngressantes,
        Double pctConclusao,
        Long rankNaRegiao
) {}
