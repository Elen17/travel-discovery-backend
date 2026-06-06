-- Promote the free-text hotels.hotel_type column to a proper foreign key into a
-- HOTEL_TYPE reference table.

CREATE TABLE IF NOT EXISTS HOTEL_TYPE
(
    ID   SERIAL PRIMARY KEY,
    NAME VARCHAR(30) NOT NULL UNIQUE
);

INSERT INTO HOTEL_TYPE (ID, NAME)
VALUES (1, 'Hotel'),
       (2, 'Villa'),
       (3, 'Apartment'),
       (4, 'Resort'),
       (5, 'Guest House'),
       (6, 'Hostel'),
       (7, 'Motel'),
       (8, 'Other')
ON CONFLICT (ID) DO NOTHING;

-- Keep the SERIAL sequence ahead of the explicit ids we just inserted.
SELECT setval(pg_get_serial_sequence('hotel_type', 'id'),
              (SELECT MAX(id) FROM hotel_type));

ALTER TABLE hotels
    ADD COLUMN IF NOT EXISTS type_id INT;

-- Backfill from the old string column. EnumType.STRING stored the enum constant
-- names (e.g. 'GUEST_HOUSE'); the types we no longer model as first-class
-- (holiday home, B&B, chalet, apart-hotel) collapse to 'Other'.
UPDATE hotels
SET type_id = CASE UPPER(hotel_type)
                  WHEN 'HOTEL'       THEN 1
                  WHEN 'VILLA'       THEN 2
                  WHEN 'APARTMENT'   THEN 3
                  WHEN 'RESORT'      THEN 4
                  WHEN 'GUEST_HOUSE' THEN 5
                  WHEN 'HOSTEL'      THEN 6
                  WHEN 'MOTEL'       THEN 7
                  ELSE 8
              END
WHERE type_id IS NULL;

-- Anything still unset (shouldn't happen — hotel_type was NOT NULL) defaults to 'Other'.
UPDATE hotels SET type_id = 8 WHERE type_id IS NULL;

ALTER TABLE hotels
    ALTER COLUMN type_id SET NOT NULL,
    ADD CONSTRAINT fk_hotels_type_id FOREIGN KEY (type_id) REFERENCES hotel_type (id);

-- Index moves from the old text column to the new FK.
DROP INDEX IF EXISTS idx_hotels_type;
CREATE INDEX IF NOT EXISTS idx_hotels_type ON hotels (type_id);

-- The string column is now redundant and would block JPA inserts (it was NOT NULL
-- but is no longer mapped by the entity).
ALTER TABLE hotels DROP COLUMN IF EXISTS hotel_type;