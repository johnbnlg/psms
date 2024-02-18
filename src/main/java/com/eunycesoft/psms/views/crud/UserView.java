package com.eunycesoft.psms.views.crud;

import com.eunycesoft.psms.data.entity.User;
import com.eunycesoft.psms.views.MainLayout;
import com.eunycesoft.psms.views.components.gridcrud.Crud;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@PageTitle("User")
@Route(value = "User", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UserView extends Crud<User> {
    public UserView() {
        super(User.class);
    }
}

