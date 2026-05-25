CREATE TABLE users (
    id                      BIGSERIAL       PRIMARY KEY,
    full_name               VARCHAR(100)    NOT NULL,
    email                   VARCHAR(255)    NOT NULL UNIQUE,
    password_hash           VARCHAR(255),
    avatar_url              VARCHAR(500),
    home_country            VARCHAR(100),
    preferred_currency      CHAR(3)         NOT NULL DEFAULT 'USD',
    preferred_language      VARCHAR(5)      NOT NULL DEFAULT 'en',
    role                    VARCHAR(20)     NOT NULL DEFAULT 'USER',
    google_id               VARCHAR(255),
    notification_bookings   BOOLEAN         NOT NULL DEFAULT TRUE,
    notification_inspiration BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email    ON users(email);
CREATE INDEX idx_users_google   ON users(google_id);
