package com.eunycesoft.psms.data.entity;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.data.AbstractEntity;
import com.eunycesoft.psms.data.enums.*;
import com.eunycesoft.psms.data.repository.StudentMarkRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"classroom", "subject", "language"})})
public class ClassroomSubject extends AbstractEntity {
    @NotNull
    @Enumerated(EnumType.STRING)
    private Classroom classroom;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Language language;
    @NotNull
    @Enumerated(EnumType.STRING)
    private SubjectGroup subjectGroup;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Subject subject;
    @NotNull
    private Double markedOver;
    private Double oralRatio, writtenRatio, practicalRatio, attitudeRatio;


    @OneToMany(mappedBy = "classroomSubject", cascade = ALL, orphanRemoval = true)
    private List<StudentMark> studentMarks = new ArrayList<>();

    public ClassroomSubject(Classroom classroom, Subject subject, Language language, Double markedOver) {
        this.classroom = classroom;
        this.subject = subject;
        this.language = language;
        this.markedOver = markedOver;
        this.subjectGroup = subject.getSubjectGroup();
        oralRatio = subject.getDefaultOralRatio();
        writtenRatio = subject.getDefaultWrittenRatio();
        practicalRatio = subject.getDefaultPracticalRatio();
        attitudeRatio = subject.getDefaultAttitudeRatio();
    }

    public ClassroomSubject(Classroom classroom, Subject subject, Language language, Double markedOver,
                            Double oralRatio, Double writtenRatio, Double practicalRatio, Double attitudeRatio) {
        this.classroom = classroom;
        this.language = language;
        this.subjectGroup = subject.getSubjectGroup();
        this.subject = subject;
        this.markedOver = markedOver;
        this.oralRatio = oralRatio;
        this.writtenRatio = writtenRatio;
        this.practicalRatio = practicalRatio;
        this.attitudeRatio = attitudeRatio;
    }

    public String toString() {
        var fm = new DecimalFormat("#.#");
        return String.format("%s /%s", subject.toString(), fm.format(markedOver));
    }

    public String toString(Language language) {
        var fm = new DecimalFormat("#.#");
        return String.format("%s /%s", subject.toString(language), fm.format(markedOver));
    }


    @PreRemove
    public void preRemove() {
        StudentMarkRepository repo = (StudentMarkRepository) Application.repositories.getRepositoryFor(StudentMark.class).get();
        repo.deleteByClassroomSubject(this);
    }

    @PostPersist
    public void postPersist() {
        StudentMarkRepository repo = (StudentMarkRepository) Application.repositories.getRepositoryFor(StudentMark.class).get();
        for (Period period : Period.getMonths()) {
            repo.saveAll(classroom.getStudents().stream().map(sdt -> new StudentMark(period, sdt, this)).toList());
        }
    }
}