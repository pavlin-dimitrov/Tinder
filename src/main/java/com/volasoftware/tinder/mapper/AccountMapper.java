package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.dto.AccountDto;
import com.volasoftware.tinder.entity.Account;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

  AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

  Account mapAccountDtoToAccount(AccountDto accountDto);

  AccountDto mapAccountToAccountDto(Account account);

  List<AccountDto> mapAccountListToAccountDtoList(List<Account> list);
}
