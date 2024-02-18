package com.eunycesoft.psms.views.crud;

import com.eunycesoft.psms.data.entity.StudentResultByGroup;
import com.eunycesoft.psms.views.MainLayout;
import com.eunycesoft.psms.views.components.gridcrud.Crud;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;

@PageTitle("StudentResultByGroup")
@Route(value = "StudentResultByGroup", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class StudentResultByGroupView extends Crud<StudentResultByGroup> {
	public StudentResultByGroupView() {
		super(StudentResultByGroup.class);
	}
}

