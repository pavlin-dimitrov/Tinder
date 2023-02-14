package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.enums.AccountType;
import com.volasoftware.tinder.enums.Role;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountRegisterMapper {

  String DEFAULT_IMAGE_LINK =
      "https://drive.google.com/file/d/1W1viYGAN02JMMPbBnbewuaCdR9OHQS1r/view?usp=share_link";

  AccountRegisterMapper INSTANCE = Mappers.getMapper(AccountRegisterMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isVerified", ignore = true)
  @Mapping(target = "image", ignore = true)
  @Mapping(target = "location", ignore = true)
  @Mapping(target = "verificationTokens", ignore = true)
  @Mapping(target = "friends", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "type", ignore = true)
  Account dtoToAccount(AccountRegisterDTO accountRegisterDTO);

  @AfterMapping
  @BeanMapping(builder = @Builder(disableBuilder = true))
  default void setDefaultImageLink(@MappingTarget Account.AccountBuilder account) {
    account
        .image(DEFAULT_IMAGE_LINK)
        .role(Role.USER)
        .type(AccountType.REAL)
        .build();
  }

  AccountRegisterDTO accountToDto(Account account);
}
