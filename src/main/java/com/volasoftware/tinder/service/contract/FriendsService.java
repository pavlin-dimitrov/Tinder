package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.DTO.FriendDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import java.security.Principal;
import java.util.List;

public interface FriendsService {

  ResponseDTO linkingAllRealAccountsWithRandomFriends();

  ResponseDTO linkingRequestedRealAccountWithRandomFriends(Long id);

  List<FriendDTO> showAllMyFriendsOrderedByClosestLocation(Principal principal);
}
