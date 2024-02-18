package com.eunycesoft.psms.data.repository;

import com.eunycesoft.psms.data.ExtendedJpaRepository;
import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.data.entity.StudentFee;
import com.eunycesoft.psms.data.enums.Fee;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface StudentFeeRepository extends ExtendedJpaRepository<StudentFee, Integer> {
    long deleteByStudentAndFee(Student student, Fee fee);

    long deleteByStudent(Student student);

}

