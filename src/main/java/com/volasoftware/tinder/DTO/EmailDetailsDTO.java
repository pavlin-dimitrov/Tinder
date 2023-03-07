package com.volasoftware.tinder.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
public class EmailDetailsDTO {
  private String recipient;
  private String msgBody;
  private String subject;
}