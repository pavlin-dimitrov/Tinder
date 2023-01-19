package com.volasoftware.tinder.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volasoftware.tinder.DTO.AccountLoginDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.auth.AuthenticationResponse;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.enums.Role;
import com.volasoftware.tinder.exception.AccountNotFoundException;
import com.volasoftware.tinder.exception.AccountNotVerifiedException;
import com.volasoftware.tinder.exception.EmailIsTakenException;
import com.volasoftware.tinder.exception.MissingRefreshTokenException;
import com.volasoftware.tinder.service.contract.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationServiceImpl implements AuthenticationService {
    private final VerificationTokenService verificationTokenService;
    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Override
    public void register(AccountRegisterDTO accountRegisterDTO) {
        log.info("Register new account with email {}", accountRegisterDTO.getEmail());
        Optional<Account> accountByEmail =
                accountService.findAccountByEmail(accountRegisterDTO.getEmail());
        if (accountByEmail.isPresent()) {
            throw new EmailIsTakenException("Email is taken! Use another e-mail address!");
        }
        Account account = modelMapper.map(accountRegisterDTO, Account.class);
        account.setPassword(passwordEncoder.encode(accountRegisterDTO.getPassword()));
        account.setRole(Role.USER);
        account = accountService.save(account);
        VerificationToken token = verificationTokenService.createVerificationToken(account);
        log.info("Verification token generated for email: {}", accountRegisterDTO.getEmail());
        try {
            emailService.sendVerificationEmail(accountRegisterDTO.getEmail(), token.getToken());
            log.info("Email with verification token sent to email: {}", accountRegisterDTO.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send email for: " + account.getEmail() + "\n" + e);
            e.printStackTrace();
        }
        modelMapper.map(account, AccountRegisterDTO.class);
    }

    @Override
    public AuthenticationResponse login(AccountLoginDTO accountLoginDTO) {
        verifyLogin(accountLoginDTO);
        log.info("Login verified.");

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        accountLoginDTO.getEmail(),
                        accountLoginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Authentication successfully added to Security Context Holder");

        var user = accountService.findAccountByEmail(accountLoginDTO.getEmail()).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);
        log.info("Access token created after the Login.");
        var refreshToken = jwtService.generateRefreshToken(user);
        log.info("Refresh token created after Login.");

        return AuthenticationResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void refresh(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader("Authorization");
        final String refresh_jwt;
        final String userEmail;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                refresh_jwt = authHeader.substring(7);
                userEmail = jwtService.extractUsername(refresh_jwt);
                Optional<Account> optionalAccount = accountService.findAccountByEmail(userEmail);
                if (optionalAccount.isEmpty()) {
                    log.error("Account was not found in /refresh endpoint");
                    throw new AccountNotFoundException("Account not found for the refresh token!");
                }
                Account account = optionalAccount.get();
                String access_jwt = jwtService.generateAccessToken(account);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("refreshToken", refresh_jwt);
                tokens.put("accessToken", access_jwt);
                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception e) {
                log.error("Error refreshing the access token: {}", e.getMessage());
                response.setHeader("error", e.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            log.warn("Refresh token is missing, can not refresh the access token.");
            throw new MissingRefreshTokenException("Refresh token is missing!");

        }
    }

    private void verifyLogin(AccountLoginDTO accountLoginDTO) {
        Optional<Account> optionalAccount = accountService.findAccountByEmail(accountLoginDTO.getEmail());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            if (!account.isVerified()) {
                throw new AccountNotVerifiedException("User is not verified!");
            }
        }
    }
}
