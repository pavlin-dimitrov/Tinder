package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.dto.LocationDto;

public interface LocationService {

  double getFriendDistance(LocationDto myLocation, LocationDto friendLocation);

}
