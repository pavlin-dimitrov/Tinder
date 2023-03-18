package com.volasoftware.tinder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class LocationDto {

  @NotNull
  @DecimalMin("-90.0")
  @DecimalMax("90.0")
  private Double latitude;

  @NotNull
  @DecimalMin("-180.0")
  @DecimalMax("180.0")
  private Double longitude;
}
