package com.euduvido.euduvido_api.domain.enums;

/**
 * Enum que representa os possíveis estados de uma participação em um desafio.
 * - INVITED: Usuário foi convidado mas ainda não respondeu
 * - ACCEPTED: Usuário aceitou o desafio
 * - REFUSED: Usuário recusou o desafio
 * - COMPLETED: Usuário completou o desafio com sucesso
 */
public enum ParticipationStatus {
    INVITED,
    ACCEPTED,
    REFUSED,
    COMPLETED
}

