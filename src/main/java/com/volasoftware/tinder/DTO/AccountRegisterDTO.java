package com.volasoftware.tinder.DTO;

import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.validator.password.ValidPassword;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "Account model", description = "Model representing an account entity")
public class AccountRegisterDTO {
    @ApiModelProperty(value = "Account's first name", required = true)
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[A-Za-z-]*$")
    @NotBlank
    @NotNull
    private String firstName;

    @ApiModelProperty(value = "Account's last name", required = true)
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[A-Za-z-]*$")
    @NotBlank
    @NotNull
    private String lastName;

    @ApiModelProperty(value = "Account's email address", required = true)
    @NotBlank
    @NotNull
    @Email
    private String email;

    @ApiModelProperty(value = "Account's password", required = true)
    @ValidPassword
    @NotBlank
    @NotNull
    private String password;

    @ApiModelProperty(value = "Account's gender", required = true)
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