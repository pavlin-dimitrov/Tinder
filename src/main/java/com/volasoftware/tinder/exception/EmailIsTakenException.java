package com.volasoftware.tinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.IM_USED)
public class EmailIsTakenException extends IllegalStateException{

  public EmailIsTakenException() {
    super("Email is taken! Use another e-mail address!");
  }
}
