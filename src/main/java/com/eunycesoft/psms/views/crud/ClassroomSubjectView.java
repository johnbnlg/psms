package com.eunycesoft.psms.views.crud;

import com.eunycesoft.psms.data.entity.ClassroomSubject;
import com.eunycesoft.psms.views.MainLayout;
import com.eunycesoft.psms.views.components.gridcrud.Crud;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@PageTitle("ClassroomSubject")
@Route(value = "ClassroomSubject", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class ClassroomSubjectView extends Crud<ClassroomSubject> {
    public ClassroomSubjectView() {
        super(ClassroomSubject.class);
    }
}

