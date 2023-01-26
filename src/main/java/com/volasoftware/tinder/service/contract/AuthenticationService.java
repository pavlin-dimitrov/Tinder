package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountLoginDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.auth.AuthenticationResponse;

import com.volasoftware.tinder.exception.NotAuthorizedException;
import java.security.Principal;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthenticationService {

  ResponseDTO register(AccountRegisterDTO request);

  AuthenticationResponse login(AccountLoginDTO accountLoginDTO);

  void getNewPairAuthTokens(HttpServletRequest request, HttpServletResponse response) throws IOException;

  ResponseDTO recoverPassword(AccountDTO accountDTO, Principal principal)
      throws NotAuthorizedException, MessagingException;
}
