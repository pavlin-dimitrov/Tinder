package com.volasoftware.tinder.DTO;

import com.volasoftware.tinder.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
}
