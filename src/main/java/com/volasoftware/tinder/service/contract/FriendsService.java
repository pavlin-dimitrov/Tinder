package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.FriendDTO;
import com.volasoftware.tinder.DTO.LocationDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import java.security.Principal;
import java.util.List;

public interface FriendsService {

  void linkFriendsAsync(Long id);

  ResponseDTO linkingAllRealAccountsWithRandomFriends();

  ResponseDTO linkingRequestedRealAccountWithRandomFriends(Long id);

  AccountDTO getFriendInfo(String email, Long friendId);

  void checkIfUsersAreFriends(Account account, Account friend);

  List<FriendDTO> showFilteredListOfFriends(String sortedBy, String orderedBy,
      Principal principal, LocationDTO locationDTO, Integer limit);
}
