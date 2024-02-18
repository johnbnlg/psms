package com.eunycesoft.psms.data.repository;

import com.eunycesoft.psms.data.ExtendedJpaRepository;
import com.eunycesoft.psms.data.entity.ClassroomTeacher;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.enums.Language;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ClassroomTeacherRepository extends ExtendedJpaRepository<ClassroomTeacher, Integer> {
    List<ClassroomTeacher> findByClassroomAndLanguage(Classroom classroom, Language language);
}

