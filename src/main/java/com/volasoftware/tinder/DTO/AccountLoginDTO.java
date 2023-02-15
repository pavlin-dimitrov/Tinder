package com.volasoftware.tinder.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AccountLoginDTO {

    @ApiModelProperty(
        value = "The email address of the account",
        example = "john.doe@example.com",
        required = true)
    private String email;

    @ApiModelProperty(
        value = "Password of account",
        example = "Aa12345678",
        required = true)
    private String password;
}
