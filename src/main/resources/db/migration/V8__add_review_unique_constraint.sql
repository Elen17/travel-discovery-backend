-- One review per user per hotel. Enables upsert semantics in ReviewService
-- (a user's second review updates their existing one rather than inserting a duplicate).
ALTER TABLE REVIEWS
    ADD CONSTRAINT UQ_REVIEWS_USER_HOTEL UNIQUE (USER_ID, HOTEL_ID);

-- Speeds up the "does this user already have a review for this hotel?" lookup
-- and the per-user review listing.
CREATE INDEX IF NOT EXISTS IDX_REVIEWS_USER_ID ON REVIEWS(USER_ID);
