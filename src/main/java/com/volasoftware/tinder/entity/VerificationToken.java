package com.volasoftware.tinder.entity;

import com.volasoftware.tinder.auditor.Auditable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
