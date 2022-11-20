package com.brainate.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Entity
@Table(name = "dictionaryLanguages")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class DictionaryLanguage {

    public interface dictionaryLanguageValidation {}

    @Id
    @Column(name = "id_dictionaryLanguage")
    @SequenceGenerator(name = "dictionary_language_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dictionary_language_seq")
    @NotNull
    private Long id;

    @Column(name = "name")
    @NotBlank(groups = {dictionaryLanguageValidation.class})
    @Length(min = 2, max = 15, groups = {dictionaryLanguageValidation.class})
    @Pattern(regexp = "^[a-zA-Z0-9_/\\- ]+$", groups = {dictionaryLanguageValidation.class})
    private String name;

    @Column(name = "languageShortcut")
    @NotBlank(groups = {dictionaryLanguageValidation.class})
    @Length(min = 2, max = 8, groups = {dictionaryLanguageValidation.class})
    @Pattern(regexp = "^[a-zA-Z0-9_/\\- ]+$", groups = {dictionaryLanguageValidation.class})
    private String languageShortcut;

    @Column(name = "codeForTranslator")
    @Length(min = 2, max = 8, groups = {dictionaryLanguageValidation.class})
    @Pattern(regexp = "^[a-zA-Z0-9_/\\- ]+$", groups = {dictionaryLanguageValidation.class})
    private String codeForTranslator;

    @Column(name = "codeForSpeech")
    @Length(min = 2, max = 8, groups = {dictionaryLanguageValidation.class})
    @Pattern(regexp = "^[a-zA-Z0-9_/\\- ]+$", groups = {dictionaryLanguageValidation.class})
    private String codeForSpeech;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "dictionaryLanguage")
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private Language language;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "dictionaryLanguage",cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<SpecialLetter> specialLetters;
}
