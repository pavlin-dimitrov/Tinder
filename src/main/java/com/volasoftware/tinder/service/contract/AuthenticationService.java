package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.DTO.AccountLoginDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.auth.AuthenticationRequest;
import com.volasoftware.tinder.auth.AuthenticationResponse;

public interface AuthenticationService {

  AccountRegisterDTO register(AccountRegisterDTO request);

  AuthenticationResponse login(AccountLoginDTO accountLoginDTO);
}
