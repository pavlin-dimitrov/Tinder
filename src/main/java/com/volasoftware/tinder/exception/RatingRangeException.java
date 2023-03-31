package com.volasoftware.tinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RatingRangeException extends IllegalArgumentException {

  public RatingRangeException() {
    super("Rating must be between 1 and 10.");
  }
}
