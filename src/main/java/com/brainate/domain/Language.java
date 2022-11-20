package com.brainate.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import java.util.List;

@Entity
@Table(name = "languages")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Language {

    public interface LanguageValidation {}

    @Id
    @Column(name = "id_language")
    @SequenceGenerator(name = "language_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "language_seq")
    @NotNull
    private long id;

    @Column(name = "hidden")
    private boolean hidden;

    @Column(name = "started")
    private boolean started;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "language",cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Category> categories;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_account")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    @JsonBackReference
    private Account account;

    @OneToOne(fetch = FetchType.EAGER, optional = false, cascade=CascadeType.ALL)
    @JoinColumn(name = "id_dictionaryLanguage")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private DictionaryLanguage dictionaryLanguage;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "language",cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<CategorySetting> categorySettings;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "language",cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Statistic> statistics;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "language",cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Task> tasks;

}
