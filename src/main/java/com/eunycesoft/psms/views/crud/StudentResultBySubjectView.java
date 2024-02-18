package com.eunycesoft.psms.views.crud;

import com.eunycesoft.psms.data.entity.StudentResultBySubject;
import com.eunycesoft.psms.views.MainLayout;
import com.eunycesoft.psms.views.components.gridcrud.Crud;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;

@PageTitle("StudentResultBySubject")
@Route(value = "StudentResultBySubject", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class StudentResultBySubjectView extends Crud<StudentResultBySubject> {
	public StudentResultBySubjectView() {
		super(StudentResultBySubject.class);
	}
}

