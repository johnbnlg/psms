package com.eunycesoft.psms.data.entity;

import com.eunycesoft.psms.data.AbstractEntity;
import com.eunycesoft.psms.data.enums.Period;
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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"period", "student", "classroom_subject"})})
public class StudentMark extends AbstractEntity {
    @NotNull
    @Enumerated(EnumType.STRING)
    private Period period;
    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(name = "student")
    private Student student;
    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(name = "classroom_subject")
    private ClassroomSubject classroomSubject;

    @Column(columnDefinition = "DEC(5,2)")
    private Double mark, oral, written, practical, attitude;

    public StudentMark(Period period, Student student, ClassroomSubject classroomSubject) {
        this.period = period;
        this.student = student;
        this.classroomSubject = classroomSubject;
    }

    public void splitMark(Double mark) {
        oral = mark * classroomSubject.getOralRatio();
        written = mark * classroomSubject.getWrittenRatio();
        practical = mark * classroomSubject.getPracticalRatio();
        attitude = mark * classroomSubject.getAttitudeRatio();
    }
}
