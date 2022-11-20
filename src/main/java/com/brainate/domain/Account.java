package com.brainate.domain;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "accounts")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Account {

    public interface AccountValidation {}

    @Id
    @Column(name = "id_account")
    @SequenceGenerator(name = "account_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @NotNull
    private Long id;

    @Column(name = "username")
    @NotBlank(groups = {AccountValidation.class})
    @Length(min = 6, max = 15, groups = {AccountValidation.class})
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", groups = {AccountValidation.class})
    private String username;

    @Column(name = "email")
    @NotBlank(groups = {AccountValidation.class})
    @Length(min = 6, max = 60, groups = {AccountValidation.class})
    @Pattern(regexp = "^[a-zA-Z0-9._-]+@([a-zA-Z0-9-_]+\\.)+[a-zA-Z0-9-_]+$", groups = {AccountValidation.class})
    private String email;

    @Column(name = "password")
    @NotBlank(groups = {AccountValidation.class})
    @Length(min = 8, max = 70, groups = {AccountValidation.class})
    private String password;

    @Column(name = "points")
    private Long points;

    @Column(name = "tasksOldestDoneDate")
    private Timestamp tasksOldestDoneDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    private List<Language> languages;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    private List<HiddenWord> hiddenWords;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id.equals(account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
