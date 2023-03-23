package com.volasoftware.tinder.repository;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.enums.AccountType;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

  @Query("SELECT a FROM Account a WHERE a.email = ?1")
  Optional<Account> findAccountByEmail(String email);

  List<Account> findAllByType(AccountType accountType);

  @Query("SELECT a FROM Account a")
  @NotNull
  Page<Account> findAll(@NotNull Pageable pageable);

//  @Query("SELECT r.friend FROM Rating r WHERE r.account = :accountId ORDER BY r.rating DESC")
//  List<Account> findFriendsByRating(Long accountId);
}
