CREATE TABLE reviews (
    id          BIGSERIAL       PRIMARY KEY,
    user_id     BIGINT          NOT NULL REFERENCES users(id),
    hotel_id    BIGINT          NOT NULL REFERENCES hotels(id),
    rating      SMALLINT        NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     TEXT,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_reviews_hotel_id ON reviews(hotel_id);

CREATE TABLE wishlist (
    id          BIGSERIAL       PRIMARY KEY,
    user_id     BIGINT          NOT NULL REFERENCES users(id),
    hotel_id    BIGINT          NOT NULL REFERENCES hotels(id),
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, hotel_id)
);

CREATE INDEX idx_wishlist_user_id ON wishlist(user_id);

CREATE TABLE events (
    id              BIGSERIAL       PRIMARY KEY,
    title           VARCHAR(200)    NOT NULL,
    description     TEXT,
    venue           VARCHAR(200)    NOT NULL,
    city            VARCHAR(100)    NOT NULL,
    event_date      DATE            NOT NULL,
    start_time      TIME,
    end_time        TIME,
    ticket_price    NUMERIC(10,2),
    is_free         BOOLEAN         NOT NULL DEFAULT FALSE,
    is_editors_pick BOOLEAN         NOT NULL DEFAULT FALSE,
    category        VARCHAR(30)     NOT NULL,
    image_url       VARCHAR(500),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_events_date     ON events(event_date);
CREATE INDEX idx_events_category ON events(category);
CREATE INDEX idx_events_city     ON events(city);
