package com.volasoftware.tinder.DTO;

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
@ApiModel(
    value = "Account verification model",
    description = "Model representing an account verification field for registration purpose.")
public class AccountVerificationDTO {

  @ApiModelProperty(value = "The verification status of email", example = "false", required = true)
  private boolean isVerified;
}