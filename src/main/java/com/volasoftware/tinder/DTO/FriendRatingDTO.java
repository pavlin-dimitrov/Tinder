package com.volasoftware.tinder.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ApiModel(value = "Rating dto model ", description = "Model representing friend rating.")
public class FriendRatingDTO {

  @ApiModelProperty(value = "Friend id", required = true)
  private Long friendId;

  @Min(value = 1, message = "Rating must be between 1 and 10")
  @Max(value = 10, message = "Rating must be between 1 and 10")
  @ApiModelProperty(value = "Friend rating")
  private int rating;
}
