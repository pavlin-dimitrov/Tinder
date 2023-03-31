package com.volasoftware.tinder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
public class EmailDetailsDto {
  private String recipient;
  private String msgBody;
  private String subject;
}