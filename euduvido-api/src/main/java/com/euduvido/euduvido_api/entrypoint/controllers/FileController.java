package com.euduvido.euduvido_api.entrypoint.controllers;

import com.euduvido.euduvido_api.application.services.FileStorageService;
import com.euduvido.euduvido_api.application.services.StoredFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "Arquivos", description = "Upload e download de arquivos de comprovação")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Operation(summary = "Fazer upload de arquivo", description = "Armazena o arquivo e retorna URL de acesso")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Arquivo armazenado"),
            @ApiResponse(responseCode = "400", description = "Arquivo ausente ou inválido")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoredFile> upload(@RequestPart("file") MultipartFile file) throws IOException {
        StoredFile stored = fileStorageService.store(file.getBytes(), file.getOriginalFilename());
        return ResponseEntity.status(HttpStatus.CREATED).body(stored);
    }

    @Operation(summary = "Baixar arquivo", description = "Retorna o arquivo pelo nome; não requer autenticação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Arquivo retornado"),
            @ApiResponse(responseCode = "400", description = "Nome de arquivo inválido"),
            @ApiResponse(responseCode = "404", description = "Arquivo não encontrado")
    })
    @SecurityRequirements
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String filename) {
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Nome de arquivo inválido");
        }
        byte[] bytes = fileStorageService.load(filename);
        String contentType = probeContentType(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(new ByteArrayResource(bytes));
    }

    private String probeContentType(String filename) {
        try {
            String probed = Files.probeContentType(Path.of(filename));
            if (probed != null) return probed;
        } catch (IOException ignored) {}
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }
}
