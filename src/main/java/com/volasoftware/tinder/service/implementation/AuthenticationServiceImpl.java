package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.auth.AuthenticationRequest;
import com.volasoftware.tinder.auth.AuthenticationResponse;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.enums.Role;
import com.volasoftware.tinder.exeption.EmailIsTakenException;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.repository.VerificationTokenRepository;
import com.volasoftware.tinder.service.contract.AuthenticationService;
import com.volasoftware.tinder.service.contract.EmailService;
import com.volasoftware.tinder.service.contract.JwtService;
import com.volasoftware.tinder.service.contract.VerificationTokenService;
import java.util.Optional;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationServiceImpl implements AuthenticationService {
  private final VerificationTokenService verificationTokenService;
  private final AuthenticationManager authenticationManager;
  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;
  private final ModelMapper modelMapper;
  private final JwtService jwtService;
  private final EmailService emailService;

  @Override
  // TODO with the registration we must send VerificationToken to the user to verify his e-mail address.
  //  After the isVerified field is set to `true`, redirect to LOGIN page;
  public AccountRegisterDTO register(AccountRegisterDTO accountRegisterDTO) {

    log.info("Register new account with email {}", accountRegisterDTO.getEmail());

    Optional<Account> accountByEmail =
        accountRepository.findAccountByEmail(accountRegisterDTO.getEmail());
    if (accountByEmail.isPresent()) {
      throw new EmailIsTakenException("Email is taken! Use another e-mail address!");
    }

    Account account = modelMapper.map(accountRegisterDTO, Account.class);
    account.setPassword(passwordEncoder.encode(accountRegisterDTO.getPassword()));
    account.setRole(Role.USER);
    account = accountRepository.save(account);

    VerificationToken token = verificationTokenService.createVerificationToken(account);
    log.info("Verification token generated for email: {}", accountRegisterDTO.getEmail());

    try {
      emailService.sendVerificationEmail(accountRegisterDTO.getEmail(), token.getToken());
      log.info("Email with verification token sent to email: {}", accountRegisterDTO.getEmail());
    } catch (MessagingException e) {
      log.error("Failed to send email for: " + account.getEmail() + "\n" + e);
      e.printStackTrace();
    }

    return modelMapper.map(account, AccountRegisterDTO.class);

//    var user =
//        Account.builder()
//            .firstName(request.getFirstName())
//            .lastName(request.getLastName())
//            .email(request.getEmail())
//            .password(passwordEncoder.encode(request.getPassword()))
//            .role(Role.USER)
//            .build();
//
//    accountRepository.save(user);
//    var jwtToken = jwtService.generateToken(user);
//    return AuthenticationResponse.builder().token(jwtToken).build();
  }

  @Override
  //TODO first check if e-mail is verified and then do other things...;
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    var user = accountRepository.findAccountByEmail(request.getEmail()).orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder().token(jwtToken).build();
  }
}
