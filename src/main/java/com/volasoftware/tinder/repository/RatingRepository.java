package com.volasoftware.tinder.repository;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.Rating;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface RatingRepository extends JpaRepository<Rating, Long> {

  Optional<Rating> findByAccountAndFriend(Account account, Account friend);

  List<Rating> findAllByAccount(Account account);

  @Query("SELECT r.friend FROM Rating r WHERE r.account.id = :accountId")
  List<Account> findRatedFriendsByAccountId(Long accountId, Pageable pageable);
}
