package com.eunycesoft.psms.views.crud;

import com.eunycesoft.psms.data.entity.StudentPayment;
import com.eunycesoft.psms.views.MainLayout;
import com.eunycesoft.psms.views.components.gridcrud.Crud;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@PageTitle("StudentPayment")
@Route(value = "StudentPayment", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class StudentPaymentView extends Crud<StudentPayment> {
    public StudentPaymentView() {
        super(StudentPayment.class);
    }
}

