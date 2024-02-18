package com.eunycesoft.psms.data.entity;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.data.AbstractEntity;
import com.eunycesoft.psms.data.enums.Fee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudentPayment extends AbstractEntity {

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(name = "student")
    private Student student;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Fee fee;

    @Positive
    private int amount;

    private LocalDate paymentDate = LocalDate.now();

    @ManyToOne(optional = false)
    @JoinColumn(name = "operator")
    private User operator;

    public StudentPayment(Student student, Fee fee, int amount) {
        this.student = student;
        this.fee = fee;
        this.amount = amount;
    }

    @PrePersist
    public void prePersist() {
        operator = Application.authenticatedUser.get().get();
        paymentDate = LocalDate.now();
    }
}
