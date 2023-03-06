package com.volasoftware.tinder.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.DTO.FriendRatingDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.Location;
import com.volasoftware.tinder.entity.Rating;
import com.volasoftware.tinder.exception.RatingRangeException;
import com.volasoftware.tinder.mapper.FriendMapper;
import com.volasoftware.tinder.mapper.RatingMapper;
import com.volasoftware.tinder.repository.RatingRepository;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.FriendsService;
import com.volasoftware.tinder.service.contract.RatingService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {

  @Autowired private RatingService underTest;
  @Mock private AccountService accountService;
  @Mock private FriendsService friendsService;
  @Mock private RatingRepository repository;

  @BeforeEach
  void setUp() {
    underTest = new RatingServiceImpl(accountService, friendsService, repository);
  }

  @Test
  @DisplayName("New Friend Rating")
  void testRateFriendWhenUserEmailAndRatingDtoIsGivenThenExpectSuccessfullyRated() {
    // given
    Account user = getAccounts().get(0);
    Account friend = getAccounts().get(1);

    FriendRatingDTO ratingDTO = new FriendRatingDTO();
    ratingDTO.setRating(6);
    ratingDTO.setFriendId(friend.getId());

    when(accountService.getAccountByEmailIfExists(user.getEmail())).thenReturn(user);
    when(accountService.getAccountByIdIfExists(friend.getId())).thenReturn(friend);
    //when
    ResponseDTO response = underTest.rateFriend(user.getEmail(), ratingDTO);
    //then
    assertThat(response.getResponse()).isEqualTo("Successfully rated friend!");
  }

  @Test
  @DisplayName("Update Friend Rating")
  void testRateFriendWhenUserEmailAndRatingDtoIsGivenThenExpectSuccessfullyUpdatedRating(){
    //given
    Account user = getAccounts().get(0);
    Account friend = getAccounts().get(1);

    Rating rating = new Rating();
    rating.setId(1L);
    rating.setAccount(user);
    rating.setFriend(friend);
    rating.setRating(6);

    FriendRatingDTO ratingDTO = RatingMapper.INSTANCE.ratingToFriendRatingDTO(rating);
    ratingDTO.setRating(7);
    ratingDTO.setFriendId(friend.getId());

    when(accountService.getAccountByEmailIfExists(user.getEmail())).thenReturn(user);
    when(accountService.getAccountByIdIfExists(friend.getId())).thenReturn(friend);
    when(repository.findByAccountAndFriend(user, friend)).thenReturn(Optional.of(rating));
    //when
    ResponseDTO response = underTest.rateFriend(user.getEmail(), ratingDTO);
    //then
    assertThat(response.getResponse()).isEqualTo("Successfully updated rating friend!");
  }

  @Test
  @DisplayName("Rating out of range")
  void testRateFriendWhenRatingIsOutOfRangeThenExpectException(){
    // given
    Account user = getAccounts().get(0);
    Account friend = getAccounts().get(1);

    FriendRatingDTO ratingDTO = new FriendRatingDTO();
    ratingDTO.setRating(11);
    ratingDTO.setFriendId(friend.getId());
    //when and then
    assertThatThrownBy(() -> underTest.rateFriend(user.getEmail(), ratingDTO))
        .isInstanceOf(RatingRangeException.class)
        .hasMessage("Rating must be between 1 and 10.");
  }

  private List<Account> getAccounts() {
    List<Account> accounts = new ArrayList<>();
    Account account1 =
        Account.builder().id(1L).firstName("Vratsa").email("john.doe@gmail.com").build();
    Account account2 =
        Account.builder().id(2L).firstName("Mezdra").email("jane.care@mail.com").build();
    Account account3 =
        Account.builder().id(3L).firstName("Sofia").email("bob.davide@gmail.com").build();

    accounts.add(account1);
    accounts.add(account2);
    accounts.add(account3);

    Set<Account> account1Friends = new HashSet<>();
    account1Friends.add(account2);
    account1Friends.add(account3);
    account1.setFriends(account1Friends);

    Set<Account> account2Friends = new HashSet<>();
    account2Friends.add(account3);
    account2.setFriends(account2Friends);

    return accounts;
  }
}
