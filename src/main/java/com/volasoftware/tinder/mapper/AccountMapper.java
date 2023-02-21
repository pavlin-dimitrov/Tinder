package com.volasoftware.tinder.mapper;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.entity.Account;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

  AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

  Account mapAccountDtoToAccount(AccountDTO accountDTO);

  AccountDTO mapAccountToAccountDto(Account account);

  List<AccountDTO> mapAccountListToAccountDtoList(List<Account> list);
}
