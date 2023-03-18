package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.dto.FriendDto;
import com.volasoftware.tinder.entity.Account;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FriendMapper {

  FriendMapper INSTANCE = Mappers.getMapper(FriendMapper.class);

  @Mapping(source = "location", target = "locationDto")
  FriendDto accountToFriendDto(Account account);

  @InheritInverseConfiguration
  Account friendDtoToAccount(FriendDto friendDto);
}
