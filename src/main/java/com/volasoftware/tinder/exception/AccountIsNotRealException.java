package com.volasoftware.tinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountIsNotRealException extends IllegalArgumentException {
    public AccountIsNotRealException() {
        super("Current account is type BOT. Can not seed friends for Accounts from type BOT");
    }
}
