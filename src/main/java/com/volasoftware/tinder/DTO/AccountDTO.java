package com.volasoftware.tinder.dto;

import com.volasoftware.tinder.enums.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ApiModel(description = "Public account information")
public class AccountDto {
  @ApiModelProperty(value = "Account ID", example = "23", required = true)
  private Long id;

  @ApiModelProperty(
      value = "The first name of the user in the account",
      example = "John",
      required = true)
  private String firstName;

  @ApiModelProperty(
      value = "The last name of the user in the account",
      example = "Doe",
      required = true)
  private String lastName;

  @ApiModelProperty(
      value = "The email address of the account",
      example = "john.doe@example.com",
      required = true)
  private String email;

  @ApiModelProperty(
      value = "The gender of user in the account",
      example = "MALE",
      required = true)
  private Gender gender;
}
