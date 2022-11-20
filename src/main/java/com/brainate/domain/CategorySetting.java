package com.brainate.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "categorySettings")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class CategorySetting {

    public interface CategorySettingValidation {}

    @Id
    @Column(name = "id_category_setting")
    @SequenceGenerator(name = "category_setting_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_setting_seq")
    @NotNull
    private Long id;

    @Column(name = "mode")
    @NotNull(groups = {CategorySettingValidation.class})
    @Enumerated(EnumType.STRING)
    private Mode mode;

    @Column(name = "thisUnitOn")
    private boolean thisUnitOn;

    @Column(name = "method")
    @NotNull(groups = {CategorySettingValidation.class})
    @Enumerated(EnumType.STRING)
    private Method method;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_language")
    @JsonBackReference
    private Language language;
}
