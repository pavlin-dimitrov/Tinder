package com.volasoftware.tinder.exception;

import com.volasoftware.tinder.DTO.ErrorResponseDTO;
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
  public ResponseEntity<ErrorResponseDTO> handleNotFoundException(RuntimeException ex) {
    return new ResponseEntity<>(
        new ErrorResponseDTO(HttpStatus.NOT_FOUND, ex.getMessage(), LocalDateTime.now()),
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
  public ResponseEntity<ErrorResponseDTO> handleBadRequestException(RuntimeException ex) {
    return new ResponseEntity<>(
        new ErrorResponseDTO(HttpStatus.BAD_REQUEST, ex.getMessage(), LocalDateTime.now()),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleException(Exception ex) {
    return new ResponseEntity<>(
        new ErrorResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", LocalDateTime.now()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
