package com.eunycesoft.psms.data.repository;

import com.eunycesoft.psms.data.ExtendedJpaRepository;
import com.eunycesoft.psms.data.entity.StudentResultBySubject;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.enums.Language;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentResultBySubjectRepository extends ExtendedJpaRepository<StudentResultBySubject, Integer> {
    List<StudentResultBySubject> findByStudent_Classroom(Classroom classroom);

    List<StudentResultBySubject> findByStudent_ClassroomAndClassroomSubject_Language(Classroom classroom, Language language);


}

