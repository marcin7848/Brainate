package com.brainate.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "statistics")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Statistic {

    @Id
    @Column(name = "id_statistic")
    @SequenceGenerator(name = "statistic_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statistic_seq")
    @NotNull
    private Long id;

    @Column(name = "mode")
    @Enumerated(EnumType.STRING)
    private Mode mode;

    @Column(name = "added")
    private Long added;

    @Column(name = "done")
    private Long done;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_language")
    @JsonBackReference
    private Language language;
}
