package com.brainate.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "hiddenWords")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class HiddenWord {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "hidden_word_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hidden_word_seq")
    @NotNull
    private long id;

    @Column(name = "id_language")
    private long id_language;

    @Column(name = "id_category")
    private long id_category;

    @Column(name = "id_wordConfig")
    private long id_wordConfig;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_account")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    @JsonBackReference
    private Account account;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HiddenWord that = (HiddenWord) o;
        return id_language == that.id_language &&
                id_category == that.id_category &&
                id_wordConfig == that.id_wordConfig &&
                account.equals(that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_language, id_category, id_wordConfig);
    }

}
