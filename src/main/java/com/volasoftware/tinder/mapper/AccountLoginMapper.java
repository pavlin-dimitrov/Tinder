package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.DTO.AccountLoginDTO;
import com.volasoftware.tinder.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountLoginMapper {

  AccountLoginMapper INSTANCE = Mappers.getMapper(AccountLoginMapper.class);

  Account mapAccountLoginDtoToAccount(AccountLoginDTO accountLoginDTO);

  AccountLoginDTO mapAccountToAccountLoginDTO(Account account);
}
