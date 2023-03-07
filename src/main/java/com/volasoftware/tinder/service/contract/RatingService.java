package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.DTO.FriendRatingDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;

public interface RatingService {

  ResponseDTO rateFriend(String email, FriendRatingDTO friendRatingDTO);

}
