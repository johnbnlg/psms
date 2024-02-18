package com.eunycesoft.psms.views.components;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.data.entity.StudentFee;
import com.eunycesoft.psms.data.entity.StudentMark;
import com.eunycesoft.psms.data.entity.StudentPayment;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.repository.StudentFeeRepository;
import com.eunycesoft.psms.data.repository.StudentMarkRepository;
import com.eunycesoft.psms.data.repository.StudentPaymentRepository;
import com.eunycesoft.psms.data.repository.StudentRepository;
import com.eunycesoft.psms.views.components.gridcrud.Form;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;

import static com.eunycesoft.psms.Application.repositories;

public class StudentClassroomUpdater extends Form {
    private Student student;
    private StudentRepository sdtRepo = (StudentRepository) repositories.getRepositoryFor(Student.class).get();
    private StudentFeeRepository sfRepo = (StudentFeeRepository) Application.repositories.getRepositoryFor(StudentFee.class).get();
    private StudentPaymentRepository spRepo = (StudentPaymentRepository) Application.repositories.getRepositoryFor(StudentPayment.class).get();
    private StudentMarkRepository smRepo = (StudentMarkRepository) Application.repositories.getRepositoryFor(StudentMark.class).get();

    private ComboBox<Classroom> classChooser;

    public StudentClassroomUpdater(Student student) {
        this.student = student;
        setTitle("Updating student's classroom");
        classChooser = new ComboBox<>("", Classroom.values());
        classChooser.setPlaceholder("Destination classroom");
        var disclaimer = new Span("Caution: Data such as marks, fees and payments will be lost.");
        disclaimer.getElement().getThemeList().add("badge error small ");
        getContent().add(
                new Span("Student's name: " + student.getFullName()),
                new Span("Student's classroom: " + student.getClassroom().getName()),
                disclaimer,
                classChooser);
    }

    @Override
    public void onSubmit() {
        if (classChooser.getValue() != null && classChooser.getValue() != student.getClassroom()) {
            sfRepo.deleteByStudent(student);
            spRepo.deleteByStudent(student);
            smRepo.deleteByStudent(student);
            student = sdtRepo.save(student);
            student.setClassroom(classChooser.getValue());
            student.resetSubjectsAndFees();
            sdtRepo.save(student);
            Utils.showSuccessNotification("Student classroom updated successfully");
            super.onSubmit();
        }
    }
}
