package com.volasoftware.tinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AccountVerificationDto {

  @ApiModelProperty(value = "The verification status of email", example = "false", required = true)
  @JsonProperty("isVerified")
  private boolean isVerified;
}