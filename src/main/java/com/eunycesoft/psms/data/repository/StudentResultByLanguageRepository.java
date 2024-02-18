package com.eunycesoft.psms.data.repository;

import com.eunycesoft.psms.data.ExtendedJpaRepository;
import com.eunycesoft.psms.data.entity.StudentResultByLanguage;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.enums.Language;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentResultByLanguageRepository extends ExtendedJpaRepository<StudentResultByLanguage, Integer> {
    List<StudentResultByLanguage> findByStudent_ClassroomAndLanguage(Classroom classroom, Language language);

    StudentResultByLanguage findByStudent_IdAndLanguage(Integer id, Language language);

    List<StudentResultByLanguage> findByLanguage(Language language);


}

