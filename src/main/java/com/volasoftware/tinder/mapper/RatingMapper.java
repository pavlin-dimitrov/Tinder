package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.DTO.FriendRatingDTO;
import com.volasoftware.tinder.entity.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RatingMapper {

  RatingMapper INSTANCE = Mappers.getMapper(RatingMapper.class);

  FriendRatingDTO ratingToFriendRatingDTO(Rating rating);

  Rating friendRatingDTOToRating(FriendRatingDTO friendRatingDTO);

}
