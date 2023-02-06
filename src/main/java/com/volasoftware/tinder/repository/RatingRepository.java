package com.volasoftware.tinder.repository;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.Rating;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {

  Optional<Rating> findByAccountAndFriend(Account account, Account friend);
}
