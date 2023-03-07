package com.volasoftware.tinder.exception;

import javax.naming.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotAuthorizedException extends AuthenticationException {

  public NotAuthorizedException() {
    super("Not authorized to edit this account!");
  }
}
