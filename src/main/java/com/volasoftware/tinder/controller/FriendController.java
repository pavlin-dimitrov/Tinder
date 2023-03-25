package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.dto.*;
import com.volasoftware.tinder.service.contract.FriendService;
import com.volasoftware.tinder.service.contract.RatingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import org.springframework.web.bind.annotation.PathVariable;
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
public class FriendController {

  private final FriendService friendService;
  private final RatingService ratingService;

  @ApiOperation(
      value = "Get list of filtered friend")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Not authorized action"),
        @ApiResponse(code = 403, message = "Accessing the resource is forbidden"),
        @ApiResponse(code = 404, message = "The resource is not found")
      })
  @GetMapping("")
  public ResponseEntity<List<FriendDto>> showFilteredListOfFriends(
      @RequestParam(value = "sortedBy", required = false, defaultValue = "location") String sortedBy,
      @RequestParam(value = "orderedBy", required = false, defaultValue = "desc") String orderedBy,
      Principal principal,
      @Valid @RequestBody(required = false) LocationDto locationDto,
      @RequestParam(value = "limit", required = false, defaultValue = "-1") Integer limit) {

    return new ResponseEntity<>(friendService.showFilteredListOfFriends(
            sortedBy, orderedBy, principal, locationDto, limit), HttpStatus.OK);
  }

  @ApiOperation(value = "Rate friend")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Successfully rated friend"),
        @ApiResponse(code = 401, message = "Not authorized action"),
        @ApiResponse(code = 403, message = "Accessing the resource is forbidden"),
        @ApiResponse(code = 404, message = "The resource is not found")
      })
  @PostMapping("/rate")
  public ResponseEntity<ResponseDto> rateFriend(
      Principal principal, @Valid @RequestBody FriendRatingDto friendRatingDto) {
    return new ResponseEntity<>(
        ratingService.rateFriend(principal.getName(), friendRatingDto), HttpStatus.OK);
  }

  @ApiOperation(value = "Get friend info")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Not authorized action"),
        @ApiResponse(code = 403, message = "Accessing the resource is forbidden"),
        @ApiResponse(code = 404, message = "The resource is not found")
      })
  @GetMapping("/info/{id}")
  public ResponseEntity<AccountDto> showFriendInfo(
      Principal principal, @PathVariable("id") Long friendId) {
    return new ResponseEntity<>(
        friendService.getFriendInfo(principal.getName(), friendId), HttpStatus.OK);
  }
}
