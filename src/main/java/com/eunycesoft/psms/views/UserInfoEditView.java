package com.eunycesoft.psms.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;

@PageTitle("My Informations")
@Route(value = "UserInfoEdit", layout = MainLayout.class)
@PermitAll
public class UserInfoEditView extends VerticalLayout {

}
