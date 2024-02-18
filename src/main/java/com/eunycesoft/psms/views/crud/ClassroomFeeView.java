package com.eunycesoft.psms.views.crud;

import com.eunycesoft.psms.data.entity.ClassroomFee;
import com.eunycesoft.psms.views.MainLayout;
import com.eunycesoft.psms.views.components.gridcrud.Crud;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@PageTitle("ClassroomFee")
@Route(value = "ClassroomFee", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class ClassroomFeeView extends Crud<ClassroomFee> {
    public ClassroomFeeView() {
        super(ClassroomFee.class);
    }
}

