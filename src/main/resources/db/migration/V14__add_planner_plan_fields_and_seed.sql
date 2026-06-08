-- Extend planner sessions with the richer "plan" fields and seed sample plans.
ALTER TABLE PLANNER_SESSIONS
    ADD COLUMN DESCRIPTION     TEXT,
    ADD COLUMN DURATION        INT,
    ADD COLUMN TYPE            VARCHAR(20),
    ADD COLUMN TRAVELERS_COUNT INT,
    ADD COLUMN IMAGE_URL       TEXT;

-- Seed plans owned by the admin user (id -1, created in V12).
-- 4 daily plans (type = 'day') and 5 hourly plans (type = 'hour').
INSERT INTO PLANNER_SESSIONS
    (SESSION_TOKEN, USER_ID, EXPLORATION_ID, TITLE, DESCRIPTION, DURATION, TYPE, TRAVELERS_COUNT, IMAGE_URL)
VALUES
    -- Daily plans
    ('sess_seed_day_01', -1, 'iceland', 'Icelandic Northern Lights',
     'Chasing the aurora across Iceland''s winter landscapes.', 12, 'day', 2,
     'https://images.unsplash.com/photo-1483347756197-71ef80e95f73'),
    ('sess_seed_day_02', -1, 'tuscany', 'Tuscany Wine Country',
     'Rolling vineyards, hilltop towns and long lunches.', 7, 'day', 2,
     'https://images.unsplash.com/photo-1523906834658-6e24ef2386f9'),
    ('sess_seed_day_03', -1, 'kyoto', 'Kyoto Temples & Gardens',
     'A slow tour of Kyoto''s shrines, gardens and tea houses.', 5, 'day', 1,
     'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e'),
    ('sess_seed_day_04', -1, 'amalfi', 'Amalfi Coast Escape',
     'Cliffside villages and the blue of the Mediterranean.', 6, 'day', 4,
     'https://images.unsplash.com/photo-1533165850316-d65a87f43afe'),
    -- Hourly plans
    ('sess_seed_hour_01', -1, 'iceland', 'Reykjavik in a Day',
     'A packed hourly walk through the Icelandic capital.', 8, 'hour', 2,
     'https://images.unsplash.com/photo-1504214208698-ea1916a2195a'),
    ('sess_seed_hour_02', -1, 'tuscany', 'Florence Art Walk',
     'Renaissance highlights, hour by hour.', 6, 'hour', 2,
     'https://images.unsplash.com/photo-1543429776-2782fc8e1acd'),
    ('sess_seed_hour_03', -1, 'kyoto', 'Kyoto Morning Temples',
     'Beat the crowds with an early hourly temple route.', 5, 'hour', 1,
     'https://images.unsplash.com/photo-1545569341-9eb8b30979d9'),
    ('sess_seed_hour_04', -1, 'amalfi', 'Positano Afternoon',
     'An easy afternoon stroll through Positano.', 4, 'hour', 4,
     'https://images.unsplash.com/photo-1534113414509-0eec2bfb493f'),
    ('sess_seed_hour_05', -1, 'iceland', 'Blue Lagoon Evening',
     'A relaxing hourly plan for a Blue Lagoon evening.', 3, 'hour', 2,
     'https://images.unsplash.com/photo-1504109586057-7a2ae83d1338');

-- A representative user/assistant message pair per seeded plan.
INSERT INTO PLANNER_MESSAGES (SESSION_ID, ROLE, CONTENT)
SELECT s.ID, 'USER', m.user_content
FROM PLANNER_SESSIONS s
JOIN (VALUES
    ('sess_seed_day_01', 'Suggest day trips near Icelandic Northern Lights'),
    ('sess_seed_day_02', 'Plan a relaxed week in Tuscany wine country'),
    ('sess_seed_day_03', 'Help me see Kyoto''s best temples and gardens'),
    ('sess_seed_day_04', 'Plan a few days along the Amalfi Coast'),
    ('sess_seed_hour_01', 'Give me an hour-by-hour day in Reykjavik'),
    ('sess_seed_hour_02', 'Plan an hourly art walk through Florence'),
    ('sess_seed_hour_03', 'An early morning hourly temple route in Kyoto'),
    ('sess_seed_hour_04', 'A relaxed afternoon in Positano'),
    ('sess_seed_hour_05', 'Plan an evening at the Blue Lagoon')
) AS m(token, user_content) ON m.token = s.SESSION_TOKEN
WHERE s.USER_ID = -1;

INSERT INTO PLANNER_MESSAGES (SESSION_ID, ROLE, CONTENT)
SELECT s.ID, 'ASSISTANT', m.assistant_content
FROM PLANNER_SESSIONS s
JOIN (VALUES
    ('sess_seed_day_01', 'Here are three great day trips: the Golden Circle, the South Coast waterfalls, and a relaxing Blue Lagoon afternoon.'),
    ('sess_seed_day_02', 'I''d split the week between Chianti, Val d''Orcia and a day in Siena, with a wine tasting or two.'),
    ('sess_seed_day_03', 'Start with Fushimi Inari and Kiyomizu-dera, then Arashiyama and the Zen gardens of the north.'),
    ('sess_seed_day_04', 'Base in Amalfi, then day-trip to Positano, Ravello and a boat tour to Capri.'),
    ('sess_seed_hour_01', '09:00 Hallgrimskirkja, 11:00 harbour walk, 13:00 lunch, 15:00 Sun Voyager, 17:00 old town.'),
    ('sess_seed_hour_02', '09:00 Uffizi, 12:00 Duomo climb, 14:00 lunch, 16:00 Ponte Vecchio and the Oltrarno.'),
    ('sess_seed_hour_03', '06:30 Fushimi Inari, 08:30 Kiyomizu-dera, 10:30 Gion stroll before the crowds.'),
    ('sess_seed_hour_04', '14:00 beach, 15:30 Santa Maria Assunta, 17:00 cliffside aperitivo.'),
    ('sess_seed_hour_05', '18:00 arrival and soak, 19:30 silica mask, 20:30 swim-up bar under the stars.')
) AS m(token, assistant_content) ON m.token = s.SESSION_TOKEN
WHERE s.USER_ID = -1;
