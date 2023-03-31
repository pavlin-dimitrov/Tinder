package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.dto.AccountLoginDto;
import com.volasoftware.tinder.dto.AccountRegisterDto;
import com.volasoftware.tinder.dto.ResponseDto;
import com.volasoftware.tinder.dto.AuthenticationResponseDto;
import java.io.IOException;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthenticationService {

  ResponseDto register(AccountRegisterDto request);

  AuthenticationResponseDto login(AccountLoginDto accountLoginDto);

  void getNewPairAuthTokens(HttpServletRequest request, HttpServletResponse response) throws IOException;

  ResponseDto recoverPassword(Principal principal);
}
