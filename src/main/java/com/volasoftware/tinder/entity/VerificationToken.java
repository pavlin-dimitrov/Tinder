package com.volasoftware.tinder.entity;

import com.volasoftware.tinder.auditor.Auditable;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "verification_tokens")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken extends Auditable<String> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String token = UUID.randomUUID().toString();

    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    @NotNull
    private Account account;

    @NotNull
    private OffsetDateTime expirationDate;
}
