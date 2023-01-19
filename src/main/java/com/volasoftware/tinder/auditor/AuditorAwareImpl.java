package com.volasoftware.tinder.auditor;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class AuditorAwareImpl implements AuditorAware<String> {

//  @Override
//  public Optional<String> getCurrentAuditor() {
//    return Optional.of("Pavlin Dimitrov");
//
//    // TODO replace "Pavlin Dimitrov" with the current auditor
//  }

  @Override
  public  Optional<String> getCurrentAuditor(){
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()){
      return Optional.empty();
    }
    return Optional.of(authentication.getPrincipal().toString());
  }
}
