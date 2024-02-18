package com.eunycesoft.psms.data.entity;

import com.eunycesoft.psms.data.AbstractResultWithTotal;
import com.eunycesoft.psms.data.enums.Language;
import com.eunycesoft.psms.data.enums.SubjectGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Entity
@Setter
@Getter
@NoArgsConstructor
//@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"student", "language", "subject_group"})})
public class StudentResultByGroup extends AbstractResultWithTotal {
    @NotNull
    @Enumerated(EnumType.STRING)
    private SubjectGroup subjectGroup;

    public StudentResultByGroup(Student student, Language language, SubjectGroup subjectGroup) {
        super(student, language);
        this.subjectGroup = subjectGroup;
    }
}