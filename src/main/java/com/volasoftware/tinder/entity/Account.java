package com.volasoftware.tinder.entity;

import com.volasoftware.tinder.auditor.Auditable;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.validator.password.ValidPassword;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Data
@Entity
@Table(name = "account")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Account extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[A-Za-z-]*$")
    @NotBlank
    @NotNull
    private String firstName;

    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[A-Za-z-]*$")
    @NotBlank @NotNull
    private String lastName;

    @NotBlank @NotNull @Email
    private String email;

    @ValidPassword
    @NotBlank @NotNull private String password;

    @Enumerated(EnumType.STRING) @NotNull private Gender gender;

    public Account(
            Long id, String firstName, String lastName, String email, String password, Gender gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
    }

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