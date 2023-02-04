package com.volasoftware.tinder.DTO;

import com.volasoftware.tinder.entity.Location;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.validator.password.ValidPassword;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@ApiModel(
    value = "Account model",
    description = "Model representing an account entity for registration purpose.")
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

  @ApiModelProperty(value = "User age", required = true)
  @NotBlank
  private int age;
}
