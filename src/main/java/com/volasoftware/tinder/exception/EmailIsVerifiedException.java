package com.volasoftware.tinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailIsVerifiedException extends RuntimeException {

    public EmailIsVerifiedException(String email) {
        super("This e-mail: " + email + " is already verified!");
    }
}
