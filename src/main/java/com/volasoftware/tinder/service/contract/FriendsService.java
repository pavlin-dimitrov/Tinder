package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.FriendDTO;
import com.volasoftware.tinder.DTO.LocationDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import java.security.Principal;
import java.util.List;

public interface FriendsService {

  ResponseDTO linkingAllRealAccountsWithRandomFriends();

  ResponseDTO linkingRequestedRealAccountWithRandomFriends(Long id);

  List<FriendDTO> showAllMyFriends(Principal principal, LocationDTO myLocation);

  AccountDTO getFriendInfo(String email, Long friendId);

  void checkIfUsersAreFriends(Account account, Account friend);
}
