package com.volasoftware.tinder.DTO;

import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.validator.password.ValidPassword;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccountRegisterDTO {
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[A-Za-z-]*$")
    @NotBlank
    @NotNull
    private String firstName;

    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[A-Za-z-]*$")
    @NotBlank
    @NotNull
    private String lastName;

    @NotBlank
    @NotNull
    @Email
    private String email;

    @ValidPassword
    @NotBlank
    @NotNull
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Gender gender;

    public AccountRegisterDTO(String firstName, String lastName, String email, String password, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
    }
}