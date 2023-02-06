package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.DTO.RateFriendDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;

public interface RatingService {

  ResponseDTO rateFriend(String email, RateFriendDTO rateFriendDTO);
}
