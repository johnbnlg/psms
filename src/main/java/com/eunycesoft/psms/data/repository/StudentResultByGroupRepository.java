package com.eunycesoft.psms.data.repository;

import com.eunycesoft.psms.data.ExtendedJpaRepository;
import com.eunycesoft.psms.data.entity.StudentResultByGroup;
import com.eunycesoft.psms.data.enums.Classroom;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentResultByGroupRepository extends ExtendedJpaRepository<StudentResultByGroup, Integer> {
    List<StudentResultByGroup> findByStudent_Classroom(Classroom classroom);

}

