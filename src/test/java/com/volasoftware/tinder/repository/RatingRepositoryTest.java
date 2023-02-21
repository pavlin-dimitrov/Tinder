package com.volasoftware.tinder.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.Rating;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RatingRepositoryTest {

  @Autowired RatingRepository underTest;
  @Autowired AccountRepository accountRepository;

  @AfterEach
  void tearDown() {
    underTest.deleteAll();
  }

  @Test
  void testIfCanFindRatingByAccountAndFriendWhenCorrectRatingIsGivenThenReturnTrue() {
    // given
    Account account = createAccount("john.doe@gmail.com");
    Account friend = createAccount("jane.smith@gmail.com");
    accountRepository.saveAll(Arrays.asList(account, friend));
    Rating rating = createRating(account, friend, 8);
    underTest.save(rating);

    // when
    Optional<Rating> foundRating = underTest.findByAccountAndFriend(account, friend);

    // then
    assertTrue(foundRating.isPresent());
    assertEquals(rating.getId(), foundRating.get().getId());
    assertEquals(rating.getRating(), foundRating.get().getRating());
  }

  @Test
  void testIfCanFindAllByAccountWhenCorrectDataIsGivenThenReturnTrue() {
    //given
    Account account = createAccount("john.doe@gmail.com");
    Account friend1 = createAccount("jane.smith@gmail.com");
    Account friend2 = createAccount("aston.martin@gmail.com");
    accountRepository.saveAll(Arrays.asList(account,friend1,friend2));
    Rating rating1 = createRating(account, friend1, 5);
    Rating rating2 = createRating(account, friend2, 7);
    Rating rating3 = createRating(friend1, friend2, 3);
    underTest.saveAll(Arrays.asList(rating1, rating2, rating3));

    //when
    List<Rating> allRatingsForAccount = underTest.findAllByAccount(account);

    //then
    assertEquals(2, allRatingsForAccount.size());
    assertEquals(rating1.getId(), allRatingsForAccount.get(0).getId());
    assertEquals(rating1.getRating(), allRatingsForAccount.get(0).getRating());
    assertEquals(rating2.getId(), allRatingsForAccount.get(1).getId());
    assertEquals(rating2.getRating(), allRatingsForAccount.get(1).getRating());
  }

  @Test
  void testIfCanFindAllRatingsWhenEmptyDataIsGivenThenReturnFalse(){
    //given
    Account account = createAccount("john.doe@gmail.com");
    accountRepository.save(account);
    //when
    List<Rating> emptyRatingsList = underTest.findAllByAccount(account);
    boolean expected = emptyRatingsList.size() > 0;
    //then
    assertThat(expected).isFalse();
  }

  private Account createAccount(String email) {
    Account account = new Account();
    account.setEmail(email);
    return account;
  }

  private Rating createRating(Account account, Account friend, int ratingVal) {
    Rating rating = new Rating();
    rating.setAccount(account);
    rating.setFriend(friend);
    rating.setRating(ratingVal);
    return rating;
  }
}
