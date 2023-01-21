package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.DTO.AccountLoginDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.auth.AuthenticationResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthenticationService {

  void register(AccountRegisterDTO request);

  AuthenticationResponse login(AccountLoginDTO accountLoginDTO);

  void getNewPairAuthTokens(HttpServletRequest request, HttpServletResponse response) throws IOException;

}
