CREATE TABLE user
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    profilePhoto VARCHAR(255),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE challenge
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(150) NOT NULL,
    description      TEXT,
    creatorId        BIGINT       NOT NULL,
    deadline         DATETIME     NOT NULL,
    locationRequired BOOLEAN   DEFAULT FALSE,
    status           ENUM('ACTIVE','COMPLETED','EXPIRED') DEFAULT 'ACTIVE',
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_challenge_creator
        FOREIGN KEY (creatorId) REFERENCES user (id)
);


CREATE TABLE user_challenge
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId      BIGINT NOT NULL,
    challengeId BIGINT NOT NULL,
    joined_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (userId, challengeId),

    CONSTRAINT fk_uc_user
        FOREIGN KEY (userId) REFERENCES user (id),

    CONSTRAINT fk_uc_challenge
        FOREIGN KEY (challengeId) REFERENCES challenge (id)
);


CREATE TABLE proof
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    challengeId  BIGINT       NOT NULL,
    userId       BIGINT       NOT NULL,
    mediaUrl     VARCHAR(255) NOT NULL,
    latitude     DECIMAL(10, 8),
    longitude    DECIMAL(11, 8),
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_proof_challenge
        FOREIGN KEY (challengeId) REFERENCES challenge (id),

    CONSTRAINT fk_proof_user
        FOREIGN KEY (userId) REFERENCES user (id),

    CONSTRAINT fk_proof_participation
        FOREIGN KEY (userId, challengeId)
            REFERENCES user_challenge (userId, challengeId)
);
