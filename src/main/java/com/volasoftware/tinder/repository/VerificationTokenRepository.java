package com.volasoftware.tinder.repository;

import com.volasoftware.tinder.entity.VerificationToken;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.UUID;

@Repository
@Transactional
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(UUID token);

  List<VerificationToken> findByExpirationDateBefore(OffsetDateTime now);
}
