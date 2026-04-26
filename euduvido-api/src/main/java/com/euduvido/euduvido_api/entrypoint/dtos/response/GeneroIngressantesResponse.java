package com.euduvido.euduvido_api.entrypoint.dtos.response;

public record GeneroIngressantesResponse(
        Integer nuAnoCenso,
        Long totalIngressantes,
        Long feminino,
        Long masculino,
        Double pctFeminino,
        Double pctMasculino
) {}
