package com.volasoftware.tinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountNotVerifiedException extends RuntimeException {
    public AccountNotVerifiedException() {
        super("Account e-mail is not verified yet!");
    }
}
