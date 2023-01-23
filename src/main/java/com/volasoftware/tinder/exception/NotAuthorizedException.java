package com.volasoftware.tinder.exception;

import javax.naming.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NotAuthorizedException extends AuthenticationException {

  public NotAuthorizedException(String explanation) {
    super(explanation);
  }
}
