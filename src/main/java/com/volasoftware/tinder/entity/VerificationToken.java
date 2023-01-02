package com.volasoftware.tinder.entity;

import com.volasoftware.tinder.auditor.Auditable;
import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "verification_tokens")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Account account;

    @Column(name = "token")
    private UUID token;

    @Column(name = "expiration_date")
    private OffsetDateTime expirationDate;
}
