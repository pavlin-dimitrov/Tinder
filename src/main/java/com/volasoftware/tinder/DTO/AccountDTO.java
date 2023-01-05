package com.volasoftware.tinder.DTO;

import com.volasoftware.tinder.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
public class AccountDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
}
