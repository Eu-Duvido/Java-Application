package com.euduvido.euduvido_api.domain.enums;

/**
 * Enum que representa os possíveis estados de um desafio.
 * - PENDING: Desafio foi criado mas ainda não está ativo
 * - ACTIVE: Desafio está ativo e aceita participações
 * - COMPLETED: Desafio foi concluído com sucesso
 * - EXPIRED: Desafio expirou sem ser concluído
 */
public enum ChallengeStatus {
    PENDING,
    ACTIVE,
    COMPLETED,
    EXPIRED
}

