-- RapidAPI quota tracker
CREATE TABLE api_quota (
    provider    VARCHAR(50)     PRIMARY KEY,
    calls_used  INT             NOT NULL DEFAULT 0,
    calls_limit INT             NOT NULL DEFAULT 500,
    reset_date  DATE
);

-- Support syncing hotels from external APIs
ALTER TABLE hotels
    ADD COLUMN IF NOT EXISTS external_id VARCHAR(100) UNIQUE;

CREATE INDEX IF NOT EXISTS idx_hotels_external_id ON hotels(external_id);
