package com.brainate.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "categories")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Category {

    public interface CategoryValidation {}

    @Id
    @Column(name = "id_category")
    @SequenceGenerator(name = "category_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @NotNull
    private Long id;

    @Column(name = "name")
    @NotBlank(groups = {CategoryValidation.class})
    @Length(min = 1, max = 30, groups = {CategoryValidation.class})
    private String name;

    @Column(name = "mode")
    @NotNull(groups = {CategoryValidation.class})
    @Enumerated(EnumType.STRING)
    private Mode mode;

    @Column(name = "id_parent")
    private long idParent;

    @Column(name = "defaultCategory", updatable = false)
    private boolean defaultCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_language")
    @JsonBackReference
    private Language language;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "CategoryWordConfig",
            joinColumns = { @JoinColumn(name = "id_category") },
            inverseJoinColumns = { @JoinColumn(name = "id_wordConfig") }
    )
    private List<WordConfig> wordConfigs = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id.equals(category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
