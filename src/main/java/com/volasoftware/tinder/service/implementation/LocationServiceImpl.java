package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.dto.LocationDto;
import com.volasoftware.tinder.service.contract.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LocationServiceImpl implements LocationService {

  @Override
  public double getFriendDistance(LocationDto myLocation, LocationDto friendLocation) {
    final int R = 6371;
    double latDistance = Math.toRadians(friendLocation.getLatitude() - myLocation.getLatitude());
    double lonDistance = Math.toRadians(friendLocation.getLongitude() - myLocation.getLongitude());
    double a =
        Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(myLocation.getLatitude()))
                * Math.cos(Math.toRadians(friendLocation.getLatitude()))
                * Math.sin(lonDistance / 2)
                * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = R * c;
    distance = Math.pow(distance, 2);
    return Math.sqrt(distance);
  }
}
