package com.volasoftware.tinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OriginGreaterThenBoundException extends IllegalArgumentException{

  public OriginGreaterThenBoundException() {
    super("Bound must be greater then origin");
  }
}
