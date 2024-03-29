package com.volasoftware.tinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoRealAccountsFoundException extends RuntimeException {
    public NoRealAccountsFoundException() {
        super("Not accounts of type REAL in the database");
    }
}
