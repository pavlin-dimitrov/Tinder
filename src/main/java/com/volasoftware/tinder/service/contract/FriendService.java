package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.dto.AccountDto;
import com.volasoftware.tinder.dto.FriendDto;
import com.volasoftware.tinder.dto.LocationDto;
import com.volasoftware.tinder.dto.ResponseDto;
import com.volasoftware.tinder.entity.Account;
import java.security.Principal;
import java.util.List;

public interface FriendService {

  void linkFriendsAsync(Long id);

  ResponseDto linkingAllRealAccountsWithRandomFriends();

  ResponseDto linkingRequestedRealAccountWithRandomFriends(Long id);

  AccountDto getFriendInfo(String email, Long friendId);

  void checkIfUsersAreFriends(Account account, Account friend);

  List<FriendDto> showFilteredListOfFriends(String sortedBy, String orderedBy,
                                            Principal principal, LocationDto locationDto, Integer limit);
}
