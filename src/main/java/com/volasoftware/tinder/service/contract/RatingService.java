package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.dto.FriendRatingDto;
import com.volasoftware.tinder.dto.ResponseDto;

public interface RatingService {

  ResponseDto rateFriend(String email, FriendRatingDto friendRatingDto);

}
