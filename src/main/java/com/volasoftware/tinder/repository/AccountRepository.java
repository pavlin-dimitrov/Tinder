package com.volasoftware.tinder.repository;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.enums.AccountType;
import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

  @Query("SELECT a FROM Account a WHERE a.email = ?1")
  Optional<Account> findAccountByEmail(String email);

  List<Account> findAllByType(AccountType accountType);

  @Query("SELECT a FROM Account a")
  List<Account> findAll(Pageable pageable);
}
