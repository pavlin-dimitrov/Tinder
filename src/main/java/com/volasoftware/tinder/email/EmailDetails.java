package com.volasoftware.tinder.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
public class EmailDetails {
  private String recipient;
  private String msgBody;
  private String subject;
}