-- Accommodation type (Hotel / Villa / Apartment / Resort ...) synced from Booking.com
ALTER TABLE hotels
    ADD COLUMN IF NOT EXISTS hotel_type VARCHAR(30) NOT NULL DEFAULT 'OTHER';

CREATE INDEX IF NOT EXISTS idx_hotels_type ON hotels(hotel_type);