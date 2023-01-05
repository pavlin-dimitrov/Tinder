package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.DTO.AccountVerificationDTO;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.VerificationTokenService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class VerificationController {

  @Autowired VerificationTokenService verificationTokenService;
  @Autowired AccountService accountService;
  @Autowired ModelMapper modelMapper;

  @ApiOperation(value = "Verification of new account e-mail address.")
  @ApiResponses(
      value = {
          @ApiResponse(code = 200, message = "Successfully verified"),
          @ApiResponse(code = 401, message = "Not authorized action"),
          @ApiResponse(
              code = 403,
              message = "Accessing the resource you were trying to reach is forbidden"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
      })
  @PostMapping("/verify")
  public ResponseEntity<?> verify(@RequestParam("token") UUID token) {
    VerificationToken verificationToken = verificationTokenService.findByToken(token);

    if (verificationToken == null || verificationToken.getExpirationDate().isBefore(OffsetDateTime.now())) {
      return ResponseEntity.badRequest().body("Token expired or invalid");
    } else {
      AccountVerificationDTO accountVerificationDTO = accountService.findById(verificationToken.getAccount().getId()).orElse(null);

      if (accountVerificationDTO == null) {
        return ResponseEntity.badRequest().body("User not found");
      }

      Long accountId = verificationToken.getAccount().getId();
      accountVerificationDTO.setVerified(true);
      accountService.updateVerificationStatus(accountId, accountVerificationDTO);

      return ResponseEntity.ok().build();
    }
  }
}