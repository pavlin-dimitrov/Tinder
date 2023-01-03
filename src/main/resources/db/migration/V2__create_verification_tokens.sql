CREATE TABLE verification_tokens (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  account_id BIGINT NOT NULL,
  token VARCHAR(36) NOT NULL,
  expiration_date DATETIME NOT NULL,
  `created_at` timestamp,
  `created_by` varchar(255) DEFAULT NULL,
  `last_modified_at` timestamp,
  `last_modified_by` varchar(255) DEFAULT NULL
);

ALTER TABLE verification_tokens
ADD CONSTRAINT fk_verification_tokens_account_id
FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE;