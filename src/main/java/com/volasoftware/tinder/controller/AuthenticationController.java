package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.DTO.AccountLoginDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.auth.AuthenticationResponse;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.AuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final AccountService accountService;
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
    public ResponseEntity<String> register(@RequestBody AccountRegisterDTO dto) {
        log.info("Received request to register new account with e-mail: " + dto.getEmail());
        authenticationService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Check your e-mail to confirm the registration");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AccountLoginDTO request) {
        log.info("Received request to login");
//        String jwt = authenticationService.login(loginDto);
        return ResponseEntity.ok(authenticationService.login(request));
    }
}
