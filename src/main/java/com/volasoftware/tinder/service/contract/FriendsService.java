package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;

public interface FriendsService {

  ResponseDTO linkingAllRealAccountsWithRandomFriends();

  ResponseDTO linkingRequestedRealAccountWithRandomFriends(Long id);

}
