package com.volasoftware.tinder.exception;

import com.volasoftware.tinder.auth.ErrorResponse;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({AccountNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException ex) {
    return new ResponseEntity<>(
        new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), LocalDateTime.now()),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({
    AccountNotVerifiedException.class,
    EmailIsTakenException.class,
    EmailIsVerifiedException.class,
    MissingFriendshipException.class,
    MissingRefreshTokenException.class,
    NotAuthorizedException.class,
    RatingRangeException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleBadRequestException(RuntimeException ex) {
    return new ResponseEntity<>(
        new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), LocalDateTime.now()),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorResponse> handleException(Exception ex) {
    return new ResponseEntity<>(
        new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", LocalDateTime.now()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
