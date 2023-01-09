package com.volasoftware.tinder.exeption;

public class EmailIsTakenException extends IllegalStateException{

  public EmailIsTakenException(String s) {
    super(s);
  }
}
