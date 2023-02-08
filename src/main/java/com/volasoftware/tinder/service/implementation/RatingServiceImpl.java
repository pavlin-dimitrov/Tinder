package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.DTO.FriendRatingDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.Rating;
import com.volasoftware.tinder.exception.RatingRangeException;
import com.volasoftware.tinder.repository.RatingRepository;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.FriendsService;
import com.volasoftware.tinder.service.contract.RatingService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RatingServiceImpl implements RatingService {

  private final AccountService accountService;
  private final FriendsService friendsService;
  private final RatingRepository ratingRepository;

  @Override
  public ResponseDTO rateFriend(String email, FriendRatingDTO friendRatingDTO) {
    ResponseDTO response = new ResponseDTO();
    Account account = accountService.getAccountByEmailIfExists(email);
    Account friend = accountService.getAccountByIdIfExists(friendRatingDTO.getFriendId());
    friendsService.checkIfUsersAreFriends(account, friend);
    int ratingValue = friendRatingDTO.getRating();
    if (ratingValue < 1 || ratingValue > 10) {
      log.warn("Rating must be between 1 and 10.");
      response.setResponse("Rating must be between 1 and 10.");
      throw new RatingRangeException("Rating must be between 1 and 10.");
    }

    Optional<Rating> rating = ratingRepository.findByAccountAndFriend(account, friend);
    if (rating.isPresent()) {
      rating.get().setRating(friendRatingDTO.getRating());
      ratingRepository.save(rating.get());
      response.setResponse("Successfully updated rating!");
      log.info("Successfully updated rating!");
    } else {
      Rating newRating = new Rating();
      newRating.setAccount(account);
      newRating.setFriend(friend);
      newRating.setRating(friendRatingDTO.getRating());
      ratingRepository.save(newRating);
      response.setResponse("Successfully rated fried!");
      log.info("Successfully rated fried!");
    }
    return response;
  }
}
