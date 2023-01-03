package com.volasoftware.tinder.entity;

import com.volasoftware.tinder.auditor.Auditable;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.validator.password.ValidPassword;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.*;
import org.hibernate.Hibernate;

@Entity
@Table(name = "account")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Account extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Gender gender;

    //TODO add this changes to Flyway sql files
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @Column(name = "verification_tokens")
    private List<VerificationToken> verificationTokens;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Account account = (Account) o;
        return id != null && Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}