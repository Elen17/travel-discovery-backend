-- FIX: Added all columns that User.java entity maps to:
-- home_country, preferred_currency, preferred_language, role,
-- notification_bookings, notification_inspiration
-- PASSWORD_HASH is nullable to support Google OAuth users

CREATE TABLE USERS (
    ID                       BIGSERIAL       PRIMARY KEY,
    FULL_NAME                VARCHAR(100)    NOT NULL,
    EMAIL                    VARCHAR(255)    NOT NULL UNIQUE,
    PASSWORD_HASH            VARCHAR(255),
    AVATAR_URL               VARCHAR(500),
    GOOGLE_ID                VARCHAR(255),
    CREATED_AT               TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    UPDATED_AT               TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX IDX_USERS_EMAIL  ON USERS(EMAIL);
CREATE INDEX IDX_USERS_GOOGLE ON USERS(GOOGLE_ID);
