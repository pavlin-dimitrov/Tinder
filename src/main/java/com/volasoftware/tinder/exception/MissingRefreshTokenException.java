package com.volasoftware.tinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class MissingRefreshTokenException extends RuntimeException {
    public MissingRefreshTokenException(String message) {
        super(message);
    }
}
