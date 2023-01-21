package com.volasoftware.tinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailIsVerifiedException extends RuntimeException {

    public EmailIsVerifiedException(String message) {
        super(message);
    }
}
