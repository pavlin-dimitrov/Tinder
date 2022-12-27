package com.volasoftware.tinder.auditor;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;

public class AuditorAwareImpl implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    return Optional.of("Pavlin Dimitrov");

    // TODO replace "Pavlin Dimitrov" with the current auditor
  }
}
