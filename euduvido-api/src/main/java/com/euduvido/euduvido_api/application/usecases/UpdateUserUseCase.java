package com.euduvido.euduvido_api.application.usecases;

import com.euduvido.euduvido_api.domain.entities.User;
import com.euduvido.euduvido_api.domain.repositories.UserRepository;


/**
 * Caso de uso: Atualizar um usuário.
 * Responsabilidade: Validar dados e atualizar usuário.
 */
public class UpdateUserUseCase {
    private final UserRepository userRepository;

    public UpdateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Executa a atualização de um usuário
     * @param email email do usuário para buscar
     * @param name Nome do usuário
     * @param profileImageUrl URL da imagem de perfil (opcional)
     * @return Usuário atualizado
     * @throws IllegalArgumentException se dados forem inválidos
     */
    public User execute(String email, String name, String profileImageUrl) {
        // Buscar usuário existente
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Atualizar campos
        existingUser.updateProfile(name, profileImageUrl);

        // Persistir usuário
        return userRepository.save(existingUser);
    }
}
