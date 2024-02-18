package com.eunycesoft.psms.views.crud;

import com.eunycesoft.psms.data.entity.Personnel;
import com.eunycesoft.psms.views.MainLayout;
import com.eunycesoft.psms.views.components.gridcrud.Crud;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@PageTitle("Personnel")
@Route(value = "Personnel", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class PersonnelView extends Crud<Personnel> {
    public PersonnelView() {
        super(Personnel.class);
    }
}

