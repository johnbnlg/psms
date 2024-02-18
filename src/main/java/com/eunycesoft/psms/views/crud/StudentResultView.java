package com.eunycesoft.psms.views.crud;

import com.eunycesoft.psms.data.entity.StudentResult;
import com.eunycesoft.psms.views.MainLayout;
import com.eunycesoft.psms.views.components.gridcrud.Crud;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;

@PageTitle("StudentResult")
@Route(value = "StudentResult", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class StudentResultView extends Crud<StudentResult> {
	public StudentResultView() {
		super(StudentResult.class);
	}
}

