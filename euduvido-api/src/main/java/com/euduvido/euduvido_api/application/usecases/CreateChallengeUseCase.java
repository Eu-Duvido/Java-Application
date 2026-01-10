package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.repositories.ChallengeRepository;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;

import java.time.LocalDateTime;

/**
 * Caso de uso: Criar um novo desafio.
 * Responsabilidade: Validar dados do desafio e persistir.
 */
public class CreateChallengeUseCase {
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    public CreateChallengeUseCase(ChallengeRepository challengeRepository, UserRepository userRepository) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
    }

    /**
     * Executa a criação de um novo desafio
     * @param creatorId ID do usuário criador
     * @param title Título do desafio
     * @param description Descrição do desafio
     * @param deadline Data limite para conclusão
     * @param locationRequired Se localização é obrigatória
     * @return Desafio criado
     * @throws IllegalArgumentException se dados são inválidos
     */
    public Challenge execute(Long creatorId, String title, String description, LocalDateTime deadline, Boolean locationRequired) {
        // Buscar usuário criador
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário criador não encontrado"));

        // Criar desafio (validações de domínio ocorrem aqui)
        Challenge newChallenge = Challenge.create(title, description, creator, deadline, locationRequired);

        // Persistir desafio
        return challengeRepository.save(newChallenge);
    }
}

