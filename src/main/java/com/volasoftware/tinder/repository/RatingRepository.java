package com.volasoftware.tinder.repository;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.Rating;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface RatingRepository extends JpaRepository<Rating, Long> {

  Optional<Rating> findByAccountAndFriend(Account account, Account friend);

  List<Rating> findAllByAccount(Account account);
}
