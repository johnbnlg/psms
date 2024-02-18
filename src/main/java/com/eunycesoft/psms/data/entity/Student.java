package com.eunycesoft.psms.data.entity;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.data.enums.*;
import com.eunycesoft.psms.data.repository.StudentFeeRepository;
import com.eunycesoft.psms.data.repository.StudentMarkRepository;
import com.eunycesoft.psms.data.repository.StudentPaymentRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.ALL;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Student extends User {
    @NotNull
    @Enumerated(EnumType.STRING)
    private Classroom classroom;
    @Enumerated(EnumType.STRING)
    private BusLine busLine;
    private Integer number;
    private boolean repeater = false;
    private boolean unfit = false;
    private boolean fatherAlive = true;
    private String fatherName;
    private Integer fatherPhone;
    @Email
    private String fatherEmail;
    private boolean motherAlive = true;
    private String motherName;
    private Integer motherPhone;
    @Email
    private String motherEmail;
    private String medicalBackground;
    private Classroom nextClassroom;
    @OneToMany(mappedBy = "student", cascade = ALL, orphanRemoval = true)
    private List<StudentFee> studentFees = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = ALL, orphanRemoval = true)
    private List<StudentPayment> studentPayments = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = ALL, orphanRemoval = true)
    private List<StudentMark> studentMarks = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<StudentResultBySubject> resultBySubjects = new ArrayList<>();
    @OneToMany(mappedBy = "student")
    private List<StudentResultByGroup> resultByGroups = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<StudentResultByLanguage> resultByLanguages = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<StudentResult> results = new ArrayList<>();


    public Student(String registrationNumber, String name, String surname, Gender gender, Language language, LocalDate dateOfBirth,
                   String placeOfBirth, Role mainRole, Classroom classroom, String fatherName, Integer fatherPhone,
                   String motherName, Integer motherPhone) {
        super(registrationNumber, name, surname, language, gender, dateOfBirth, placeOfBirth, mainRole);
        this.classroom = classroom;
        this.fatherName = fatherName;
        this.fatherPhone = fatherPhone;
        this.motherName = motherName;
        this.motherPhone = motherPhone;
    }

    @Override
    public String toString() {
        return getFullName();
    }

    @PrePersist
    public void prePersist() {
        super.prePersist();
        resetSubjectsAndFees();
    }

    public void resetSubjectsAndFees() {
        if (!studentFees.isEmpty()) {
            var sfRepo = (StudentFeeRepository) Application.repositories.getRepositoryFor(StudentFee.class).get();
            var spRepo = (StudentPaymentRepository) Application.repositories.getRepositoryFor(StudentPayment.class).get();
            studentFees.clear();
            studentPayments.clear();
            sfRepo.deleteByStudent(this);
            spRepo.deleteByStudent(this);
        }
        if (!studentMarks.isEmpty()) {
            var smRepo = (StudentMarkRepository) Application.repositories.getRepositoryFor(StudentMark.class).get();
            studentMarks.clear();
            smRepo.deleteByStudent(this);
        }

        studentFees.addAll(classroom.getFees().stream()
                .filter(ClassroomFee::isMandatory)
                .map(cf -> new StudentFee(this, cf.getFee(), cf.getAmount()))
                .toList());

        for (Period period : Period.getMonths()) {
            studentMarks.addAll(
                    classroom.getSubjects().stream()
                            .map(cs -> new StudentMark(period, this, cs))
                            .toList());
        }
    }

    public int getRawAmountToPay(List<Fee> fees) {
        return (fees.isEmpty())
                ? studentFees.stream().mapToInt(StudentFee::getAmount).sum()
                : studentFees.stream().filter(sf -> fees.contains(sf.getFee())).mapToInt(StudentFee::getAmount).sum();
    }

    public int getDiscount(List<Fee> fees) {
        return (fees.isEmpty())
                ? studentFees.stream().mapToInt(StudentFee::getDiscount).sum()
                : studentFees.stream().filter(sf -> fees.contains(sf.getFee())).mapToInt(StudentFee::getDiscount).sum();
    }

    public int getNetToPay(List<Fee> fees) {
        return (fees.isEmpty())
                ? studentFees.stream().mapToInt(StudentFee::getNetToPay).sum()
                : studentFees.stream().filter(sf -> fees.contains(sf.getFee())).mapToInt(StudentFee::getNetToPay).sum();

    }

    public int getTotalPaid(List<Fee> fees) {
        return (fees.isEmpty())
                ? studentPayments.stream().mapToInt(StudentPayment::getAmount).sum()
                : studentPayments.stream().filter(sp -> fees.contains(sp.getFee())).mapToInt(StudentPayment::getAmount).sum();

    }

    public int getLeftToPay(List<Fee> fees) {
        return getNetToPay(fees) - getTotalPaid(fees);
    }

    public List<Fee> getFees() {
        return studentFees.stream().map(sf -> sf.getFee()).collect(Collectors.toList());
    }

    public List<Fee> getUnregisteredFees() {
        return Arrays.stream(Fee.values()).filter(fee -> !studentFees.contains(fee)).toList();
    }
}
