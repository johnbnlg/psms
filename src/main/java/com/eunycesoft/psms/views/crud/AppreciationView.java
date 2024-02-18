package com.eunycesoft.psms.views.crud;

import com.eunycesoft.psms.data.entity.Appreciation;
import com.eunycesoft.psms.views.MainLayout;
import com.eunycesoft.psms.views.components.gridcrud.Crud;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import javax.annotation.security.RolesAllowed;

@PageTitle("Appreciation")
@Route(value = "Appreciation", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AppreciationView extends Crud<Appreciation> {
	public AppreciationView() {
		super(Appreciation.class);
	}
}

