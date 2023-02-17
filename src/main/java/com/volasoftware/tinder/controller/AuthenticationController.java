package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.DTO.AccountLoginDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.auth.AuthenticationResponse;
import com.volasoftware.tinder.service.contract.AuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("api/v1/auth")
@Api(value = "Authentication controller")
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @ApiOperation(value = "Create new account / registration")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Successfully registered new account"),
        @ApiResponse(code = 401, message = "Not authorized action"),
        @ApiResponse(
            code = 403,
            message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
      })
  @PostMapping("/register")
  public ResponseEntity<ResponseDTO> register(
      @ApiParam(value = "Account registration details", required = true) @Valid @RequestBody AccountRegisterDTO dto) {
    log.info("Received request to register new account with e-mail: " + dto.getEmail());
    return new ResponseEntity<>(authenticationService.register(dto), HttpStatus.OK);
  }

  @ApiOperation(value = "Login")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Successfully logged in"),
        @ApiResponse(code = 401, message = "Not authorized action"),
        @ApiResponse(code = 403, message = "Accessing is forbidden"),
        @ApiResponse(code = 404, message = "The resource is not found")
      })
  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(
      @ApiParam(value = "Login details", required = true) @RequestBody AccountLoginDTO request) {
    log.info("Received request to login");
    return ResponseEntity.ok(authenticationService.login(request));
  }

  @ApiOperation(value = "Get new Access Token")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Successfully created new access token!"),
        @ApiResponse(code = 401, message = "Not authorized action"),
        @ApiResponse(code = 403, message = "Accessing the resource is forbidden"),
        @ApiResponse(code = 404, message = "The resource is not found")
      })
  @GetMapping("/refresh")
  public void getNewPairAuthTokens(
      @ApiParam(
              value = "HttpServletRequest used to get information about the request",
              required = true)
          HttpServletRequest request,
      @ApiParam(value = "HttpServletResponse used to set the new tokens", required = true)
          HttpServletResponse response)
      throws IOException {
    log.info("Received request for new access token");
    authenticationService.getNewPairAuthTokens(request, response);
  }

  @ApiOperation(value = "Recover password")
  @ApiResponses(
      value = {
          @ApiResponse(code = 200, message = "Successfully recovered password!"),
          @ApiResponse(code = 401, message = "Not authorized action"),
          @ApiResponse(code = 403, message = "Accessing the resource is forbidden"),
          @ApiResponse(code = 404, message = "The resource is not found")
      })
  @PostMapping("/password-recovery")
  public ResponseEntity<ResponseDTO> recoverPassword(
      @ApiParam(value = "The authenticated user", required = true) Principal principal) {
    return new ResponseEntity<>(authenticationService.recoverPassword(principal), HttpStatus.OK);
  }
}
