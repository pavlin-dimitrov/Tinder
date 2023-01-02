alter table account add `verification_tokens_id` bigint NOT NULL;
alter table account add FOREIGN KEY (`verification_tokens_id`) REFERENCES `verification_tokens` (`id`);

create TABLE `verification_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `token` varchar(255) NOT NULL,
  `expiration_date` timestamp NOT NULL,
  `created_at` timestamp,
  `created_by` varchar(255) DEFAULT NULL,
  `last_modified_at` timestamp,
  `last_modified_by` varchar(255) DEFAULT NULL,
  `account_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`token`),
  CONSTRAINT fk_verification_token_account_id FOREIGN KEY (account_id) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;