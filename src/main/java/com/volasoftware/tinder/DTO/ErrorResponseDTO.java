package com.volasoftware.tinder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ErrorResponseDto {
  private final HttpStatus status;
  private final String message;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm:ss")
  private final LocalDateTime timestamp;

  public ErrorResponseDto(HttpStatus status, String message, LocalDateTime timestamp) {
    this.status = status;
    this.message = message;
    this.timestamp = LocalDateTime.now();
  }
}
