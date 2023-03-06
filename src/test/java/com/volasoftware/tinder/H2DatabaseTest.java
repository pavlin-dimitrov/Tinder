package com.volasoftware.tinder;

import static org.assertj.core.api.Assertions.assertThat;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.repository.AccountRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class H2DatabaseTest {

   @Autowired
   private AccountRepository repository;

  @BeforeEach
  void setUp() {
    // create a test entity
    Account account = new Account();
    account.setId(1L);
    account.setFirstName("Test Entity");
    account.setEmail("test.entity@gmail.com");
    repository.saveAndFlush(account);
    System.out.println("Number of accounts in the database: " + account.getEmail());
  }

  @Test
  public void testSaveAndFind() {
    // find the entity by ID
    Optional<Account> result = repository.findById(1L);

    // verify that the entity was found
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get().getFirstName()).isEqualTo("Test Entity");
    assertThat(result.get().getEmail()).isEqualTo("test.entity@gmail.com");
  }
}
