package com.eunycesoft.psms.data.repository;

import com.eunycesoft.psms.data.ExtendedJpaRepository;
import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.data.enums.Classroom;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface StudentRepository extends ExtendedJpaRepository<Student, Integer> {
    List<Student> findByClassroomIn(Collection<Classroom> classrooms);

}

