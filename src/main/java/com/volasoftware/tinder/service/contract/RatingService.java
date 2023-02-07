package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.DTO.FriendRatingDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import java.util.List;

public interface RatingService {

  ResponseDTO rateFriend(String email, FriendRatingDTO friendRatingDTO);

  List<FriendRatingDTO> showListOfAllMyRatedFriends(Account account);
}
