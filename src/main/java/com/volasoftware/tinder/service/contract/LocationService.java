package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.DTO.LocationDTO;

public interface LocationService {

  double getFriendDistance(LocationDTO myLocation, LocationDTO friendLocation);

}
