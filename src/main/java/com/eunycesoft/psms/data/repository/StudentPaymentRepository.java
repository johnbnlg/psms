package com.eunycesoft.psms.data.repository;

import com.eunycesoft.psms.data.ExtendedJpaRepository;
import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.data.entity.StudentPayment;
import com.eunycesoft.psms.data.enums.Fee;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface StudentPaymentRepository extends ExtendedJpaRepository<StudentPayment, Integer> {
    List<StudentPayment> findByFeeInAndPaymentDateBetween(Collection<Fee> fees, LocalDate from, LocalDate to);

    long deleteByStudentAndFee(Student student, Fee fee);

    long deleteByStudent(Student student);
}

