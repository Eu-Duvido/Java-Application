CREATE TABLE usuario
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    fotoPerfil VARCHAR(255),
    criado_em  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE desafio
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo           VARCHAR(150) NOT NULL,
    descricao        TEXT,
    criadorId        BIGINT       NOT NULL,
    dataLimite       DATETIME     NOT NULL,
    localObrigatorio BOOLEAN   DEFAULT FALSE,
    status           ENUM('ACTIVE','COMPLETED','EXPIRED') DEFAULT 'ACTIVE',
    criado_em        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_desafio_criador
        FOREIGN KEY (criadorId) REFERENCES usuario (id)
);


CREATE TABLE usuario_desafio
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuarioId   BIGINT NOT NULL,
    desafioId   BIGINT NOT NULL,
    dataEntrada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (usuarioId, desafioId),

    CONSTRAINT fk_ud_usuario
        FOREIGN KEY (usuarioId) REFERENCES usuario (id),

    CONSTRAINT fk_ud_desafio
        FOREIGN KEY (desafioId) REFERENCES desafio (id)
);


CREATE TABLE comprovacao
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    desafioId BIGINT       NOT NULL,
    usuarioId BIGINT       NOT NULL,
    midiaUrl  VARCHAR(255) NOT NULL,
    latitude  DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    dataEnvio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comp_desafio
        FOREIGN KEY (desafioId) REFERENCES desafio (id),

    CONSTRAINT fk_comp_usuario
        FOREIGN KEY (usuarioId) REFERENCES usuario (id),


    CONSTRAINT fk_comp_participacao
        FOREIGN KEY (usuarioId, desafioId)
            REFERENCES usuario_desafio (usuarioId, desafioId)
);