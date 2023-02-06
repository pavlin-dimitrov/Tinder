CREATE TABLE ratings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  account_id BIGINT NOT NULL,
  friend_id BIGINT NOT NULL,
  rating INT NOT NULL,
  FOREIGN KEY (account_id) REFERENCES account(id),
  FOREIGN KEY (friend_id) REFERENCES account(id),
  CHECK (rating >= 1 AND rating <= 10)
);
