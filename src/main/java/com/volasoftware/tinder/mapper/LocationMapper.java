package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.dto.LocationDto;
import com.volasoftware.tinder.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper {

  LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

  LocationDto locationToLocationDto(Location location);

  Location locationDtoToLocation(LocationDto locationDto);
}
