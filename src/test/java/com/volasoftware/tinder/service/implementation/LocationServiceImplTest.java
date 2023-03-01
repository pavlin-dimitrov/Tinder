package com.volasoftware.tinder.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.volasoftware.tinder.DTO.LocationDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.Location;
import com.volasoftware.tinder.mapper.LocationMapper;
import com.volasoftware.tinder.service.contract.LocationService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

  @Autowired private LocationService underTest;

  @BeforeEach
  void setUp() {
    underTest = new LocationServiceImpl();
  }

  @Test
  @DisplayName("Calculate distance between two users.")
  void getFriendDistance() {
    // given
    List<LocationDTO> locationsDto =
        getAccounts().stream()
            .map(Account::getLocation)
            .map(LocationMapper.INSTANCE::locationToLocationDTO)
            .collect(Collectors.toList());
    List<Double> distances = new ArrayList<>();
    //when
    for (int location = 0; location < locationsDto.size() - 1; location++) {
      double distance = underTest.getFriendDistance(locationsDto.get(location), locationsDto.get(location + 1));
      distances.add(distance);

      System.out.println("Distance between: " +
          getAccounts().get(location).getFirstName() + " and " +
          getAccounts().get(location+1).getFirstName() + " = "+ distance);
    }
    //then
    assertThat(distances.get(1)).isGreaterThan(distances.get(0));
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

    Location location1 =
        Location.builder()
            .id(1L)
            .account(account1)
            .latitude(43.200328)
            .longitude(23.553883)
            .build();
    Location location2 =
        Location.builder()
            .id(2L)
            .account(account2)
            .latitude(43.142382)
            .longitude(23.707429)
            .build();
    Location location3 =
        Location.builder()
            .id(3L)
            .account(account3)
            .latitude(42.652041)
            .longitude(23.347395)
            .build();

    account1.setLocation(location1);
    account2.setLocation(location2);
    account3.setLocation(location3);

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
