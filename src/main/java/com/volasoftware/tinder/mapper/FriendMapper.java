package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.DTO.FriendDTO;
import com.volasoftware.tinder.entity.Account;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FriendMapper {

  FriendMapper INSTANCE = Mappers.getMapper(FriendMapper.class);

  @Mapping(source = "location", target = "locationDTO")
  FriendDTO accountToFriendDTO(Account account);

  @InheritInverseConfiguration
  Account friendDTOToAccount(FriendDTO friendDTO);
}
