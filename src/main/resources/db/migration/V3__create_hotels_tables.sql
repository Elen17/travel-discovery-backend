CREATE TABLE hotels (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(200)    NOT NULL,
    description     TEXT,
    country         VARCHAR(100)    NOT NULL,
    city            VARCHAR(100)    NOT NULL,
    address         VARCHAR(300),
    latitude        NUMERIC(9,6),
    longitude       NUMERIC(9,6),
    price_per_night NUMERIC(10,2)   NOT NULL,
    star_rating     SMALLINT        NOT NULL CHECK (star_rating BETWEEN 1 AND 5),
    main_image_url  VARCHAR(500),
    is_featured     BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_hotels_city    ON hotels(city);
CREATE INDEX idx_hotels_country ON hotels(country);
CREATE INDEX idx_hotels_price   ON hotels(price_per_night);
CREATE INDEX idx_hotels_rating  ON hotels(star_rating);

CREATE TABLE hotel_amenities (
    hotel_id        BIGINT          NOT NULL REFERENCES hotels(id) ON DELETE CASCADE,
    amenity_type    VARCHAR(30)     NOT NULL,
    PRIMARY KEY (hotel_id, amenity_type)
);

CREATE TABLE hotel_images (
    id          BIGSERIAL       PRIMARY KEY,
    hotel_id    BIGINT          NOT NULL REFERENCES hotels(id) ON DELETE CASCADE,
    image_url   VARCHAR(500)    NOT NULL,
    sort_order  INT             NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_hotel_images_hotel ON hotel_images(hotel_id);
