package com.brainate.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "wordConfigs")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class WordConfig {
    public interface WordConfigValidation {}

    @Id
    @Column(name = "id_wordConfig")
    @SequenceGenerator(name = "word_config_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_config_seq")
    @NotNull
    private Long id;

    @Column(name = "done")
    private Integer done;

    @Column(name = "today")
    private boolean today;

    @Column(name = "repeated")
    private Integer repeated;

    @Column(name = "thisUnit")
    private boolean thisUnit;

    @Column(name = "dateTimeDone")
    private Timestamp dateTimeDone;

    @Column(name = "processNo")
    private Integer processNo;

    @Column(name = "lastPerfectNum")
    private Integer lastPerfectNum;

    @Column(name = "lastPerfect")
    private boolean lastPerfect;

    @Column(name = "comment")
    private String comment;

    @Column(name = "mechanism")
    @NotNull(groups = {WordConfigValidation.class})
    @Enumerated(EnumType.STRING)
    private Mechanism mechanism;

    @Column(name = "accepted")
    private boolean accepted;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "CategoryWordConfig",
            joinColumns = { @JoinColumn(name = "id_wordConfig") },
            inverseJoinColumns = { @JoinColumn(name = "id_category") }
    )
    @JsonBackReference
    List<Category> categories = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "wordConfig", cascade = CascadeType.ALL)
    private List<Word> words;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordConfig that = (WordConfig) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @JsonProperty
    public Long getCategoryId() {
        if(categories == null || categories.isEmpty()){
            return null;
        }
        return categories.get(0).getId();
    }

    @JsonProperty
    public Mode getCategoryMode() {
        if(categories == null || categories.isEmpty()){
            return null;
        }
        return categories.get(0).getMode();
    }
}
