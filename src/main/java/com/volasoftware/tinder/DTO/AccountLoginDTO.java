package com.volasoftware.tinder.dto;

import com.volasoftware.tinder.validator.password.ValidPassword;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AccountLoginDto {

    @ApiModelProperty(
        value = "The email address of the account",
        example = "john.doe@example.com",
        required = true)
    @NotBlank
    @Email
    private String email;

    @ApiModelProperty(
        value = "Password of account",
        example = "Aa12345678",
        required = true)
    @ValidPassword
    private String password;
}
