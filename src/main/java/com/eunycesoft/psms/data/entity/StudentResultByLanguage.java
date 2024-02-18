package com.eunycesoft.psms.data.entity;

import com.eunycesoft.psms.data.AbstractResultWithTotal;
import com.eunycesoft.psms.data.enums.Language;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"student", "language"})})
public class StudentResultByLanguage extends AbstractResultWithTotal {
    public StudentResultByLanguage(Student student, Language language) {
        super(student, language);
    }
}