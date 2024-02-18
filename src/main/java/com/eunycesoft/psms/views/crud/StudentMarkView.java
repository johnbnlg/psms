package com.eunycesoft.psms.views.crud;

import com.eunycesoft.psms.data.entity.StudentMark;
import com.eunycesoft.psms.views.MainLayout;
import com.eunycesoft.psms.views.components.gridcrud.Crud;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@PageTitle("StudentMark")
@Route(value = "StudentMark", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class StudentMarkView extends Crud<StudentMark> {
    public StudentMarkView() {
        super(StudentMark.class);
    }
}

