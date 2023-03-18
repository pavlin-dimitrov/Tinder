package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.dto.FriendRatingDto;
import com.volasoftware.tinder.entity.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RatingMapper {

  RatingMapper INSTANCE = Mappers.getMapper(RatingMapper.class);

  FriendRatingDto ratingToFriendRatingDto(Rating rating);

  Rating friendRatingDtoToRating(FriendRatingDto friendRatingDto);

}
