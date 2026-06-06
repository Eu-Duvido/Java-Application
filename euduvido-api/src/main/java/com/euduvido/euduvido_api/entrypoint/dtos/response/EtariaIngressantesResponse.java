package com.euduvido.euduvido_api.entrypoint.dtos.response;

public record EtariaIngressantesResponse(
        Long totalIngressantes,
        Long faixa017,
        Long faixa1824,
        Long faixa2529,
        Long faixa3034,
        Long faixa3539,
        Long faixa4049,
        Long faixa5059,
        Long faixa60mais,
        Double pct1824
) {}
