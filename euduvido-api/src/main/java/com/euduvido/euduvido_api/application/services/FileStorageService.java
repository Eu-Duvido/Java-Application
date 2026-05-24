package com.euduvido.euduvido_api.application.services;

public interface FileStorageService {
    /** Persiste os bytes e retorna metadados do arquivo armazenado. */
    StoredFile store(byte[] bytes, String originalFilename);

    /** Carrega os bytes de um arquivo já armazenado pelo nome gerado. */
    byte[] load(String filename);
}
