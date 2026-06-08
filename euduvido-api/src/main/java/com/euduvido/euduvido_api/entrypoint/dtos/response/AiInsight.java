package com.euduvido.euduvido_api.entrypoint.dtos.response;

public record AiInsight(
        String icon,
        String label,
        String text,
        String category,
        String color
) {}
