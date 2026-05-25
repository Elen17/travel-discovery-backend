CREATE TABLE bookings (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    hotel_id        BIGINT          NOT NULL REFERENCES hotels(id),
    check_in        DATE            NOT NULL,
    check_out       DATE            NOT NULL,
    guest_count     SMALLINT        NOT NULL CHECK (guest_count >= 1),
    total_price     NUMERIC(10,2)   NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    cancelled_at    TIMESTAMPTZ,
    special_requests TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_checkout_after_checkin CHECK (check_out > check_in)
);

CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_status  ON bookings(status);
