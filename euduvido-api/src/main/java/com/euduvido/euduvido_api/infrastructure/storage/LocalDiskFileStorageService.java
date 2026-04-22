package com.euduvido.euduvido_api.infrastructure.storage;

import com.euduvido.euduvido_api.application.services.FileStorageService;
import com.euduvido.euduvido_api.application.services.StoredFile;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalDiskFileStorageService implements FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalDiskFileStorageService.class);

    private final Path storageDir;

    public LocalDiskFileStorageService(
            @Value("${app.storage.local.path:./uploads}") String storagePath) {
        this.storageDir = Paths.get(storagePath).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(storageDir);
            log.info("[Storage] Diretório de uploads: {}", storageDir);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar diretório de uploads: " + storageDir, e);
        }
    }

    @Override
    public StoredFile store(byte[] bytes, String originalFilename) {
        String filename = UUID.randomUUID() + extractExtension(originalFilename);
        try {
            Files.write(storageDir.resolve(filename), bytes);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar arquivo: " + filename, e);
        }
        return new StoredFile(filename, "/api/v1/files/" + filename);
    }

    @Override
    public byte[] load(String filename) {
        Path resolved = storageDir.resolve(filename).normalize();
        if (!resolved.startsWith(storageDir)) {
            throw new IllegalArgumentException("Acesso não permitido");
        }
        try {
            return Files.readAllBytes(resolved);
        } catch (IOException e) {
            throw new IllegalArgumentException("Arquivo não encontrado: " + filename);
        }
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null) return "";
        int dot = originalFilename.lastIndexOf('.');
        return (dot >= 0 && dot < originalFilename.length() - 1)
                ? originalFilename.substring(dot)
                : "";
    }
}
