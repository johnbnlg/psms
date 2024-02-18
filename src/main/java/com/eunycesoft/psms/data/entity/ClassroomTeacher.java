package com.eunycesoft.psms.data.entity;

import com.eunycesoft.psms.data.AbstractEntity;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"classroom", "teacher", "language"})})
public class ClassroomTeacher extends AbstractEntity {
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(updatable = false)
    private Classroom classroom;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Language language;
    @ManyToOne
    @JoinColumn(name = "teacher")
    private Personnel teacher;

}