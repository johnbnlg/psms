package com.eunycesoft.psms.data.entity;

import com.eunycesoft.psms.data.AbstractResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"student", "classroom_subject"})})
public class StudentResultBySubject extends AbstractResult {
    @ManyToOne(optional = false)
    @JoinColumn(name = "classroom_subject")
    private ClassroomSubject classroomSubject;

    public StudentResultBySubject(Student student, ClassroomSubject classroomSubject) {
        super(student);
        this.classroomSubject = classroomSubject;
    }
}