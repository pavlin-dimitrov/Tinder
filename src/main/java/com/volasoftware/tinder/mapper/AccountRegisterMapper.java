package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.dto.AccountRegisterDto;
import com.volasoftware.tinder.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountRegisterMapper {

  AccountRegisterMapper INSTANCE = Mappers.getMapper(AccountRegisterMapper.class);

  Account mapAccountRegisterDtoToAccount(AccountRegisterDto accountRegisterDto);

  AccountRegisterDto mapAccountToAccountDto(Account account);
}
