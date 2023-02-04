package com.volasoftware.tinder.DTO;

import com.volasoftware.tinder.enums.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ApiModel(value = "Friend model", description = "Model representing friend model.")
public class FriendDTO {
  @ApiModelProperty(value = "Friend first name", required = true)
  private String firstName;

  @ApiModelProperty(value = "Friend last name", required = true)
  private String lastName;

  @ApiModelProperty(value = "Friend image", required = true)
  private String image;

  @ApiModelProperty(value = "Friend gender", required = true)
  private Gender gender;

  @ApiModelProperty(value = "Friend age", required = true)
  private int age;

  @ApiModelProperty(value = "Friend location", required = true)
  private LocationDTO locationDTO;
}
