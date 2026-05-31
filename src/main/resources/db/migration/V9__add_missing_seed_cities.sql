-- Cities referenced by seeded demo hotels that were missing from the V7 reference
-- data. Without these, location validation would reject searches for those hotels.
-- (Country name fixes — UK -> United Kingdom, Czech Republic -> Czechia — were made
--  in HotelDataSeeder to match the existing country reference rows.)

-- Santorini -> Greece (country_id 66)
INSERT INTO city (name, country_id)
SELECT 'Santorini', 66
WHERE NOT EXISTS (
    SELECT 1 FROM city WHERE LOWER(name) = LOWER('Santorini') AND country_id = 66
);

-- Prague -> Czechia (Czech Republic) (country_id 44)
INSERT INTO city (name, country_id)
SELECT 'Prague', 44
WHERE NOT EXISTS (
    SELECT 1 FROM city WHERE LOWER(name) = LOWER('Prague') AND country_id = 44
);