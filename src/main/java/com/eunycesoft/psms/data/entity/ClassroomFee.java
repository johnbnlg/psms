package com.eunycesoft.psms.data.entity;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.data.AbstractEntity;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.enums.Fee;
import com.eunycesoft.psms.data.repository.StudentFeeRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"classroom", "fee"})})
public class ClassroomFee extends AbstractEntity {
    @NotNull
    @Enumerated(EnumType.STRING)
    private Classroom classroom;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Fee fee;

    private boolean mandatory = true;

    @NotNull
    private int amount;

    @PreRemove
    public void preRemove() {
        var sfRepo = (StudentFeeRepository) Application.repositories.getRepositoryFor(StudentFee.class).get();
        classroom.getStudents().forEach(sdt -> sfRepo.deleteByStudentAndFee(sdt, fee));
    }

    @PostPersist
    public void postPersist() {
        var sfRepo = (StudentFeeRepository) Application.repositories.getRepositoryFor(StudentFee.class).get();
        sfRepo.saveAll(classroom.getStudents().stream().map(sdt -> new StudentFee(sdt, fee, amount)).toList());
    }
}
