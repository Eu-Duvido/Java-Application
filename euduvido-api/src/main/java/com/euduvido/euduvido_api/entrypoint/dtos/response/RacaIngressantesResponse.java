package com.euduvido.euduvido_api.entrypoint.dtos.response;

public record RacaIngressantesResponse(
        Long totalIngressantes,
        Long branca,
        Long preta,
        Long parda,
        Long amarela,
        Long indigena,
        Long corNaoDeclarada,
        Double pctBranca,
        Double pctPreta,
        Double pctParda,
        Double pctPretoPardo
) {}
