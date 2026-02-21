package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.entities.ChallengeParticipation;
import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.enums.ParticipationStatus;
import com.euduvido.euduvido_api.domain.repositories.ChallengeParticipationRepository;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;

/**
 * Caso de uso: Atualizar a participação de um desafio.
 * Responsabilidade: Atualizar a participação de um desafio.
 */
public class UpdateChallengeParticipationUseCase {
    private final ChallengeParticipationRepository participationRepository;
    private final UserRepository userRepository;

    public UpdateChallengeParticipationUseCase(ChallengeParticipationRepository participationRepository, UserRepository userRepository) {
        this.participationRepository = participationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Executa a criação de uma nova participação de desafio
     * @param user usuário participante
     * @param challenge desafio associado
     * @param status da participação
     * @return Participação atualizaad
     * @throws IllegalArgumentException se dados são inválidos
     */

    public ChallengeParticipation execute(Long id, User user, Challenge challenge, ParticipationStatus status) {
        // Procura a participação existente
        var existingParticipation = participationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participação não encontrada com id: " + id));

        // Verifica se o usuário existe
        if(!userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Usuário inexistente");
        }

        // Cria uma nova instância de participação com os dados atualizados
        var updatedParticipation = ChallengeParticipation.createFromDatabase(
                existingParticipation.getId(),
                user,
                challenge,
                status,
                existingParticipation.getCreatedAt()
        );

        // Persiste a participação atualizada
        return participationRepository.save(updatedParticipation);
    }
}
