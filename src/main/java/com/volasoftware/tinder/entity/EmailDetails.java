package com.volasoftware.tinder.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {
  private String recipient;
  private String msgBody;
  private String subject;
}