package com.eunycesoft.psms.views.crud;

import com.eunycesoft.psms.data.entity.StudentFee;
import com.eunycesoft.psms.views.MainLayout;
import com.eunycesoft.psms.views.components.gridcrud.Crud;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@PageTitle("StudentFee")
@Route(value = "StudentFee", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class StudentFeeView extends Crud<StudentFee> {
    public StudentFeeView() {
        super(StudentFee.class);
    }
}

