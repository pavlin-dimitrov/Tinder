create TABLE friends (
  account_id BIGINT NOT NULL,
  friend_id BIGINT NOT NULL,
  PRIMARY KEY (account_id, friend_id),
  FOREIGN KEY (account_id) REFERENCES account(id),
  FOREIGN KEY (friend_id) REFERENCES account(id)
);
