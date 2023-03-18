package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.dto.ResponseDto;
import com.volasoftware.tinder.service.contract.FriendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@RequestMapping("api/v1/seed-friends")
@Api(value = "Friends seeder controller")
public class FriendSeederController {

  private final FriendService friendService;

  @ApiOperation(value = "Seed friends")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Successfully seeded friends"),
        @ApiResponse(code = 404, message = "The resource is not found")
      })
  @GetMapping("")
  public ResponseEntity<ResponseDto> seedFriends(@RequestParam(required = false) Long accountId) {
    if (accountId == null) {
      return new ResponseEntity<>(
          friendService.linkingAllRealAccountsWithRandomFriends(), HttpStatus.OK);
    }
    return new ResponseEntity<>(
        friendService.linkingRequestedRealAccountWithRandomFriends(accountId), HttpStatus.OK);
  }
}
