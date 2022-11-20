package com.brainate.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "specialLetters")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class SpecialLetter {

    public interface specialLetterValidation {}

    @Id
    @Column(name = "id_specialLetter")
    @SequenceGenerator(name = "special_letter_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "special_letter_seq")
    @NotNull
    private Long id;

    @Column(name = "letter")
    @NotBlank(groups = {specialLetterValidation.class})
    @Length(min = 1, max = 4, groups = {specialLetterValidation.class})
    private String letter;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_dictionaryLanguage")
    @JsonBackReference
    private DictionaryLanguage dictionaryLanguage;

}
