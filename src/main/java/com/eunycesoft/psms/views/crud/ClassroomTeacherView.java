package com.eunycesoft.psms.views.crud;

import com.eunycesoft.psms.data.entity.ClassroomTeacher;
import com.eunycesoft.psms.views.MainLayout;
import com.eunycesoft.psms.views.components.gridcrud.Crud;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;

@PageTitle("ClassroomTeacher")
@Route(value = "ClassroomTeacher", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class ClassroomTeacherView extends Crud<ClassroomTeacher> {
	public ClassroomTeacherView() {
		super(ClassroomTeacher.class);
	}
}

