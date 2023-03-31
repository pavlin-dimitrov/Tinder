package com.volasoftware.tinder.exception;

import com.volasoftware.tinder.dto.ErrorResponseDto;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({AccountNotFoundException.class})
  public ResponseEntity<ErrorResponseDto> handleNotFoundException(RuntimeException ex) {
    return new ResponseEntity<>(
        new ErrorResponseDto(HttpStatus.NOT_FOUND, ex.getMessage(), LocalDateTime.now()),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({
    AccountNotVerifiedException.class,
    EmailIsTakenException.class,
    EmailIsVerifiedException.class,
    MissingFriendshipException.class,
    MissingRefreshTokenException.class,
    NotAuthorizedException.class,
    RatingRangeException.class,
    InvalidPasswordException.class
  })
  public ResponseEntity<ErrorResponseDto> handleBadRequestException(RuntimeException ex) {
    return new ResponseEntity<>(
        new ErrorResponseDto(HttpStatus.BAD_REQUEST, ex.getMessage(), LocalDateTime.now()),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleException(Exception ex) {
    return new ResponseEntity<>(
        new ErrorResponseDto(
            HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", LocalDateTime.now()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
