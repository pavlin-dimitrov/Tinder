package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.service.contract.FriendsService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("api/v1")
@Api(value = "Friends controller")
public class FriendsController {

  private final FriendsService friendsService;

  @GetMapping("/seed-friends")
  public ResponseEntity<ResponseDTO> seedFriends(@RequestParam(required = false) Long accountId) {
    if (accountId == null) {
      return new ResponseEntity<>(friendsService.linkingAllRealAccountsWithRandomFriends(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(
          friendsService.linkingRequestedRealAccountWithRandomFriends(accountId), HttpStatus.OK);
    }
  }
}
