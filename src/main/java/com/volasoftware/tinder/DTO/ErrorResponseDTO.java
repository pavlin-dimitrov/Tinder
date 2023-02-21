package com.volasoftware.tinder.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponseDTO {
  private final HttpStatus status;
  private final String message;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm:ss")
  private final LocalDateTime timestamp;

  public ErrorResponseDTO(HttpStatus status, String message, LocalDateTime timestamp) {
    this.status = status;
    this.message = message;
    this.timestamp = LocalDateTime.now();
  }
}