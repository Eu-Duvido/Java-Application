CREATE TABLE users
(
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    name              VARCHAR(100) NOT NULL,
    email             VARCHAR(150) NOT NULL,
    password          VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE challenges
(
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    title             VARCHAR(150) NOT NULL,
    description       TEXT         NOT NULL,
    difficulty        VARCHAR(10),
    subject           VARCHAR(100),
    goal_type         VARCHAR(20),
    goal_value        INT,
    deadline          DATETIME     NOT NULL,
    status            VARCHAR(50)  NOT NULL,
    location_required BOOLEAN,
    created_at        DATETIME     NOT NULL,
    creator_id        BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_challenges_creator FOREIGN KEY (creator_id) REFERENCES users (id)
);

CREATE TABLE challenge_participants
(
    challenge_id BIGINT NOT NULL,
    user_id      BIGINT NOT NULL,
    PRIMARY KEY (challenge_id, user_id),
    CONSTRAINT fk_cp_challenge FOREIGN KEY (challenge_id) REFERENCES challenges (id),
    CONSTRAINT fk_cp_user      FOREIGN KEY (user_id)      REFERENCES users (id)
);

CREATE TABLE challenge_participations
(
    id           BIGINT      NOT NULL AUTO_INCREMENT,
    user_id      BIGINT      NOT NULL,
    challenge_id BIGINT      NOT NULL,
    status       VARCHAR(50) NOT NULL,
    progress     INT         NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uq_participation             UNIQUE (user_id, challenge_id),
    CONSTRAINT fk_participation_user        FOREIGN KEY (user_id)      REFERENCES users (id),
    CONSTRAINT fk_participation_challenge   FOREIGN KEY (challenge_id) REFERENCES challenges (id)
);

CREATE TABLE proofs
(
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    participation_id BIGINT       NOT NULL,
    media_url        VARCHAR(255) NOT NULL,
    media_type       VARCHAR(50)  NOT NULL,
    latitude         DOUBLE,
    longitude        DOUBLE,
    submitted_at     DATETIME     NOT NULL,
    approved         BOOLEAN      NOT NULL DEFAULT FALSE,
    rejection_reason TEXT,
    ai_valid         BOOLEAN,
    ai_confidence    DOUBLE,
    ai_reason        TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_proofs_participation FOREIGN KEY (participation_id) REFERENCES challenge_participations (id)
);
