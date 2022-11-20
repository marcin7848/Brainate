package com.brainate.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "words")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Word {

    public interface WordValidation {}

    @Id
    @Column(name = "id_word")
    @SequenceGenerator(name = "word_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_seq")
    @NotNull
    private Long id;

    @Column(name = "word")
    private String word;

    @Column(name = "basicWord")
    private String basicWord;

    @Column(name = "seat")
    private Integer seat;

    @Column(name = "toSpeech")
    private boolean toSpeech;

    @Column(name = "answer")
    private boolean answer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_wordConfig")
    @JsonBackReference
    @ToString.Exclude
    private WordConfig wordConfig;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word1 = (Word) o;
        return answer == word1.answer &&
                Objects.equals(word.toLowerCase(), word1.word.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, answer);
    }

}
