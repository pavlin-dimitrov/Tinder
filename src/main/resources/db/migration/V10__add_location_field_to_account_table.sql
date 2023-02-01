ALTER TABLE account ADD COLUMN location_id BIGINT;
ALTER TABLE account
ADD CONSTRAINT fk_account_location
FOREIGN KEY (location_id)
REFERENCES locations (id);