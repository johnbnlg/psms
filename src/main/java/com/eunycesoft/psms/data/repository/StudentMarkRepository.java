package com.eunycesoft.psms.data.repository;

import com.eunycesoft.psms.data.ExtendedJpaRepository;
import com.eunycesoft.psms.data.entity.ClassroomSubject;
import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.data.entity.StudentMark;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.enums.Period;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface StudentMarkRepository extends ExtendedJpaRepository<StudentMark, Integer> {
    List<StudentMark> findByStudent_Classroom(Classroom classroom);

    @Query(" select s from StudentMark s where s.period = ?1 and s.classroomSubject = ?2 order by s.student.number")
    List<StudentMark> findByPeriodClassroomSubject(Period period, ClassroomSubject cs);

    long deleteByClassroomSubject(ClassroomSubject classroomSubject);

    long deleteByStudent(Student student);
}

