package com.brainate.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "tasks")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Task {

    @Id
    @Column(name = "id_tasks")
    @SequenceGenerator(name = "task_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @NotNull
    private Long id;

    @Column(name = "taskMode")
    @Enumerated(EnumType.STRING)
    @NotNull
    private TaskMode taskMode;

    @Column(name = "toDo")
    private Integer toDo;

    @Column(name = "done")
    private Integer done;

    @Column(name = "minNumber")
    private Integer minNumber;

    @Column(name = "maxNumber")
    private Integer maxNumber;

    @Column(name = "active")
    private boolean active;

    @Column(name = "taskDate")
    private Timestamp taskDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_language")
    @JsonBackReference
    private Language language;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
