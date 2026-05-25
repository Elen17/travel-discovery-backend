CREATE TABLE destinations (
    id                  BIGSERIAL       PRIMARY KEY,
    slug                VARCHAR(100)    NOT NULL UNIQUE,
    name                VARCHAR(100)    NOT NULL,
    country             VARCHAR(100)    NOT NULL,
    tagline             VARCHAR(300),
    hero_image_url      VARCHAR(500),
    avg_price_per_night NUMERIC(10,2),
    is_trending         BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_destinations_slug     ON destinations(slug);
CREATE INDEX idx_destinations_trending ON destinations(is_trending);
