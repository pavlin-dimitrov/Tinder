package com.volasoftware.tinder.DTO;

import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.validator.password.ValidPassword;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.*;

@AllArgsConstructor
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
    private String firstName;
    @ApiModelProperty(value = "Account's last name", required = true)
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[A-Za-z-]*$")
    @NotBlank
    private String lastName;
    @ApiModelProperty(value = "Account's email address", required = true)
    @NotBlank
    @Email
    private String email;
    @ApiModelProperty(value = "Account's password", required = true)
    @ValidPassword
    @NotBlank
    private String password;
    @ApiModelProperty(value = "Account's gender", required = true)
    @Enumerated(EnumType.STRING)
    @NotBlank
    private Gender gender;
}