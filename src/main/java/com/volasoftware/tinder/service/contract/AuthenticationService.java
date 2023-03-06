package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.DTO.AccountLoginDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.DTO.AuthenticationResponseDTO;
import java.io.IOException;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthenticationService {

  ResponseDTO register(AccountRegisterDTO request);

  AuthenticationResponseDTO login(AccountLoginDTO accountLoginDTO);

  void getNewPairAuthTokens(HttpServletRequest request, HttpServletResponse response) throws IOException;

  ResponseDTO recoverPassword(Principal principal);
}
