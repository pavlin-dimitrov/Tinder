package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.DTO.AccountVerificationDTO;
import com.volasoftware.tinder.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountVerificationMapper {

  AccountVerificationMapper INSTANCE = Mappers.getMapper(AccountVerificationMapper.class);

  void updateAccountFromVerificationDTO(AccountVerificationDTO verificationDTO, @MappingTarget Account account);

  AccountVerificationDTO accountToAccountVerificationDto(Account account);
}
