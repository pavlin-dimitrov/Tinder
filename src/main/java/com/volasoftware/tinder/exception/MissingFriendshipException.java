package com.volasoftware.tinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MissingFriendshipException extends IllegalArgumentException {

  public MissingFriendshipException() {
    super("You are not friend with this user, cannot rate it!");
  }
}
