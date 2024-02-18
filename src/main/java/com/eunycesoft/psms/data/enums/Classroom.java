package com.eunycesoft.psms.data.enums;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.data.entity.ClassroomFee;
import com.eunycesoft.psms.data.entity.ClassroomSubject;
import com.eunycesoft.psms.data.entity.ClassroomTeacher;
import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.data.repository.ClassroomFeeRepository;
import com.eunycesoft.psms.data.repository.ClassroomSubjectRepository;
import com.eunycesoft.psms.data.repository.ClassroomTeacherRepository;
import com.eunycesoft.psms.data.repository.StudentRepository;
import lombok.Getter;

import java.util.List;

import static com.eunycesoft.psms.data.enums.Cycle.MAT;
import static com.eunycesoft.psms.data.enums.Cycle.PRIM;
import static com.eunycesoft.psms.data.enums.Section.*;

@Getter
public enum Classroom {

    PN(1, "PS/PN", BIL, MAT, false),
    NS1(2, "MS/NS1", BIL, MAT, false),
    NS2(3, "GS/NS2", BIL, MAT, false),
    SIL_BIL(4, "SIL/Class 1", BIL, PRIM, false),
    CP_BIL(5, "CP/Class 2", BIL, PRIM, false),
    CE1_BIL(6, "CE1/Class 3", BIL, PRIM, false),
    CE2_BIL(7, "CE2/Class 4", BIL, PRIM, false),
    CM1_BIL(8, "CM1/Class 5", BIL, PRIM, false),
    CM2_BIL(9, "CM2/Class 6", BIL, PRIM, true),

    SIL(10, "SIL", FRA, PRIM, false),
    CP(11, "CP", FRA, PRIM, false),
    CE1(12, "CE1", FRA, PRIM, false),
    CE2(13, "CE2", FRA, PRIM, false),
    CM1(14, "CM1", FRA, PRIM, false),
    CM2(15, "CM2", FRA, PRIM, true),

    CLASS1(16, "Class 1", ANG, PRIM, false),
    CLASS2(17, "Class 2", ANG, PRIM, false),
    CLASS3(18, "Class 3", ANG, PRIM, false),
    CLASS4(19, "Class 4", ANG, PRIM, false),
    CLASS5(20, "Class 5", ANG, PRIM, false),
    CLASS6(21, "Class 6", ANG, PRIM, true);


    private final int id;
    private final String name;
    private final Section section;
    private final Cycle cycle;
    private final boolean examClass;


    Classroom(int id, String name, Section section, Cycle cycle, boolean examClass) {
        this.id = id;
        this.name = name;
        this.section = section;
        this.cycle = cycle;
        this.examClass = examClass;
    }

    @Override
    public String toString() {
        return name;
    }

    public List<Student> getStudents() {
        var repo = (StudentRepository) Application.repositories.getRepositoryFor(Student.class).get();
        return repo.findByClassroomIn(List.of(this));
    }

    public List<ClassroomSubject> getSubjects(Language language) {
        var repo = (ClassroomSubjectRepository) Application.repositories.getRepositoryFor(ClassroomSubject.class).get();
        return repo.findByClassroomAndLanguage(this, language);
    }

    public List<ClassroomSubject> getSubjects() {
        var repo = (ClassroomSubjectRepository) Application.repositories.getRepositoryFor(ClassroomSubject.class).get();
        return repo.findByClassroom(this);
    }

    public List<ClassroomFee> getFees() {
        var repo = (ClassroomFeeRepository) Application.repositories.getRepositoryFor(ClassroomFee.class).get();
        return repo.findByClassroom(this);
    }

    public String getTeacherName(Language language) {
        var repo = (ClassroomTeacherRepository) Application.repositories.getRepositoryFor(ClassroomTeacher.class).get();
        var list = repo.findByClassroomAndLanguage(this, language);
        if (!list.isEmpty()) {
            return list.get(0).getTeacher().getCivilName();
        } else {
            return "";
        }
    }

}
