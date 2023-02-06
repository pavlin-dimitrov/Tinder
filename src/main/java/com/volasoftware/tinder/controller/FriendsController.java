package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.DTO.FriendDTO;
import com.volasoftware.tinder.DTO.LocationDTO;
import com.volasoftware.tinder.DTO.RateFriendDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.service.contract.FriendsService;
import com.volasoftware.tinder.service.contract.RatingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("api/v1/friends")
@Api(value = "Friends controller")
public class FriendsController {

  private final FriendsService friendsService;
  private final RatingService ratingService;

  @ApiOperation(
      value =
          "Get list of all friends ordered by shortest distance if my location is passed, else not ordered list")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Not authorized action"),
        @ApiResponse(code = 403, message = "Accessing the resource is forbidden"),
        @ApiResponse(code = 404, message = "The resource is not found")
      })
  @GetMapping("")
  public ResponseEntity<List<FriendDTO>> showListOfFriends(
      @ApiParam(value = "The authenticated user") Principal principal,
      @RequestBody(required = false) LocationDTO locationDTO) {
    return new ResponseEntity<>(
        friendsService.showAllMyFriends(principal, locationDTO), HttpStatus.OK);
  }

  @PostMapping("/rate")
  public ResponseEntity<ResponseDTO> rateFriend(
      Principal principal, @Valid @RequestBody RateFriendDTO rateFriendDTO) {
    return new ResponseEntity<>(
        ratingService.rateFriend(principal.getName(), rateFriendDTO), HttpStatus.OK);
  }
}
