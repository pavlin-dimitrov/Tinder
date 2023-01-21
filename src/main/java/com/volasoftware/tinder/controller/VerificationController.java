package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.service.contract.EmailVerificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@AllArgsConstructor
@NoArgsConstructor
@RequestMapping("api/v1/verify-email")
@Slf4j
public class VerificationController {

    @Autowired
    private EmailVerificationService emailVerificationService;

    @ApiOperation(value = "Verification of new account e-mail address.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successfully verified"),
                    @ApiResponse(code = 400, message = "Token expired or invalid"),
                    @ApiResponse(code = 401, message = "Not authorized action"),
                    @ApiResponse(
                            code = 403,
                            message = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
            })
    @PostMapping("/verify")
    public RedirectView verifyAccountEmail(@RequestParam("token") String token)
            throws AccountNotFoundException {
        log.info("Received request to verify email with token: {}", token);
        emailVerificationService.verifyEmail(token);
        return new RedirectView("/api/v1/auth/login");
    }

    @ApiOperation(value = "Re-Verification of new account e-mail address.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Successfully verified"),
                    @ApiResponse(code = 400, message = "Token expired or invalid"),
                    @ApiResponse(code = 401, message = "Not authorized action"),
                    @ApiResponse(code = 403, message = "Accessing the resource is forbidden"),
                    @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
            })
    @PostMapping("/resend-verification-email")
    public ResponseEntity<String> resendVerificationEmail(@RequestParam("email") String email)
            throws AccountNotFoundException {
        log.info("Received request to RESEND verification email");
        emailVerificationService.resendVerificationEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body("Check your e-mail to confirm the registration");
    }
}