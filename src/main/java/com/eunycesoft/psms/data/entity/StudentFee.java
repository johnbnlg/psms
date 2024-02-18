package com.eunycesoft.psms.data.entity;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.data.AbstractEntity;
import com.eunycesoft.psms.data.enums.Fee;
import com.eunycesoft.psms.data.repository.StudentPaymentRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"student", "fee"})})
public class StudentFee extends AbstractEntity {

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(name = "student")
    private Student student;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Fee fee;

    @Positive
    @NotNull
    private int amount;
    @NotNull
    @PositiveOrZero
    private int discount;

    private String discountReason;

    private transient int netToPay;

    @NotNull
    private LocalDate dateline;

    public StudentFee(Student student, Fee fee, int amount) {
        this.student = student;
        this.fee = fee;
        this.amount = amount;
        this.dateline = fee.getDateline();
    }

    public int getNetToPay() {
        return amount - discount;
    }

    @Override
    public String toString() {
        return String.format("%s(%,d)", student.toString(), fee.toString(), getNetToPay());
    }

    @PreRemove
    public void preRemove() {
        var spRepo = (StudentPaymentRepository) Application.repositories.getRepositoryFor(StudentPayment.class).get();
        spRepo.deleteByStudentAndFee(student, fee);
    }
}
