package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountRegisterMapper {

  AccountRegisterMapper INSTANCE = Mappers.getMapper(AccountRegisterMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isVerified", ignore = true)
  @Mapping(target = "image", ignore = true)
  @Mapping(target = "location", ignore = true)
  @Mapping(target = "verificationTokens", ignore = true)
  @Mapping(target = "friends", ignore = true)
  @Mapping(target = "role", expression = "java(Role.USER)")
  @Mapping(target = "type", expression = "java(Type.REAL)")
  Account dtoToAccount(AccountRegisterDTO accountRegisterDTO);

  AccountRegisterDTO accountToDto(Account account);
}
