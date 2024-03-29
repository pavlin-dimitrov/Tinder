package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.dto.FriendRatingDto;
import com.volasoftware.tinder.dto.ResponseDto;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.Rating;
import com.volasoftware.tinder.exception.RatingRangeException;
import com.volasoftware.tinder.repository.RatingRepository;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.FriendService;
import com.volasoftware.tinder.service.contract.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RatingServiceImpl implements RatingService {

  private final AccountService accountService;
  private final FriendService friendService;
  private final RatingRepository ratingRepository;

  @Override
  public ResponseDto rateFriend(String email, FriendRatingDto friendRatingDto) {
    Account account = accountService.getAccountByEmailIfExists(email);
    Account friend = accountService.getAccountByIdIfExists(friendRatingDto.getFriendId());
    friendService.checkIfUsersAreFriends(account, friend);

    validateRatingRange(friendRatingDto.getRating());
    String ratingStatus = getRatingStatus(account, friend);
    setRatingForFriend(friendRatingDto, account, friend);

    ResponseDto response = new ResponseDto();
    response.setResponse("Successfully " + ratingStatus + " friend!");
    log.info(response.getResponse());

    return response;
  }

  private void setRatingForFriend(FriendRatingDto friendRatingDto, Account account, Account friend) {
    Rating rating =
        ratingRepository
            .findByAccountAndFriend(account, friend)
            .orElseGet(() -> createRating(account, friend, friendRatingDto.getRating()));
    rating.setRating(friendRatingDto.getRating());
    ratingRepository.save(rating);
  }

  private String getRatingStatus(Account account, Account friend) {
    return ratingRepository.findByAccountAndFriend(account, friend).isEmpty()
        ? "rated"
        : "updated rating";
  }

  private void validateRatingRange(int ratingValue) {
    if (ratingValue < 1 || ratingValue > 10) {
      log.warn("Rating must be between 1 and 10.");
      throw new RatingRangeException();
    }
  }

  private Rating createRating(Account account, Account friend, int ratingValue) {
    Rating newRating = new Rating();
    newRating.setAccount(account);
    newRating.setFriend(friend);
    newRating.setRating(ratingValue);
    ratingRepository.save(newRating);
    return newRating;
  }
}
