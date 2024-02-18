package com.eunycesoft.psms.views.crud;

import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.views.MainLayout;
import com.eunycesoft.psms.views.components.gridcrud.Crud;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@PageTitle("Student")
@Route(value = "Student", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class StudentView extends Crud<Student> {
    public StudentView() {
        super(Student.class);
    }
}

