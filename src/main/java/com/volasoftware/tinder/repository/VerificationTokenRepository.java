package com.volasoftware.tinder.repository;

import com.volasoftware.tinder.entity.VerificationToken;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import javax.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

  VerificationToken findByToken(String token);

  List<VerificationToken> findByExpirationDateBefore(OffsetDateTime now);

  void deleteAllByExpirationDateBefore(OffsetDateTime current);

}
