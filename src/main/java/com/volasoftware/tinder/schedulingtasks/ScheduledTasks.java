package com.volasoftware.tinder.schedulingtasks;

import com.volasoftware.tinder.repository.VerificationTokenRepository;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ScheduledTasks {

  private final VerificationTokenRepository verificationTokenRepository;

  @Scheduled(fixedDelay = 30000)
  public void deleteExpiredVerificationTokens(){
    OffsetDateTime currentDateTime = OffsetDateTime.now();
    verificationTokenRepository.deleteAllByExpirationDateBefore(
        currentDateTime);
  }
}
