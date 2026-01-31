CREATE TABLE users
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    profile_photo VARCHAR(255),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE challenges
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    title             VARCHAR(150) NOT NULL,
    description       TEXT,
    creator_id        BIGINT NOT NULL,
    deadline          DATETIME NOT NULL,
    location_required TINYINT(1) DEFAULT 0,
    status            VARCHAR(50) NOT NULL,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_challenges_creator
        FOREIGN KEY (creator_id) REFERENCES users (id)
);

CREATE TABLE challenge_participations
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    challenge_id BIGINT NOT NULL,
    status       VARCHAR(50) NOT NULL,
    joined_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (user_id, challenge_id),

    CONSTRAINT fk_participation_user
        FOREIGN KEY (user_id) REFERENCES users (id),

    CONSTRAINT fk_participation_challenge
        FOREIGN KEY (challenge_id) REFERENCES challenges (id)
);

CREATE TABLE proofs
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    participation_id BIGINT NOT NULL,
    media_url       VARCHAR(255) NOT NULL,
    media_type      VARCHAR(50),
    latitude        DECIMAL(10, 8),
    longitude       DECIMAL(11, 8),
    submitted_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_proofs_participation
        FOREIGN KEY (participation_id) REFERENCES challenge_participations (id)
);

CREATE TABLE invites
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id    BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    message      TEXT,
    accepted     TINYINT(1) DEFAULT 0,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_invites_sender
        FOREIGN KEY (sender_id) REFERENCES users (id),

    CONSTRAINT fk_invites_recipient
        FOREIGN KEY (recipient_id) REFERENCES users (id)
);
