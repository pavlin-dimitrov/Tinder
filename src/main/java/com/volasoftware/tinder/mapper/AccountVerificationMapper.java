package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.dto.AccountVerificationDto;
import com.volasoftware.tinder.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountVerificationMapper {

  AccountVerificationMapper INSTANCE = Mappers.getMapper(AccountVerificationMapper.class);

  void updateAccountFromVerificationDto(AccountVerificationDto verificationDto, @MappingTarget Account account);

  AccountVerificationDto accountToAccountVerificationDto(Account account);
}
