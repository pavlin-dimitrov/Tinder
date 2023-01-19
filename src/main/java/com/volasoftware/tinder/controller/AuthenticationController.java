package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.DTO.AccountLoginDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.auth.AuthenticationResponse;
import com.volasoftware.tinder.service.contract.AuthenticationService;
import com.volasoftware.tinder.service.contract.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    public ResponseEntity<String> register(@RequestBody AccountRegisterDTO dto) {
        log.info("Received request to register new account with e-mail: " + dto.getEmail());
        authenticationService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Check your e-mail to confirm the registration");
    }

    @ApiOperation(value = "Login")
    @ApiResponses(value = {
                    @ApiResponse(code = 200, message = "Successfully logged in"),
                    @ApiResponse(code = 401, message = "Not authorized action"),
                    @ApiResponse(code = 403, message = "Accessing is forbidden"),
                    @ApiResponse(code = 404, message = "The resource is not found")
            })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AccountLoginDTO request) {
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
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Received request for new access token");
        authenticationService.refresh(request, response);
    }
}
