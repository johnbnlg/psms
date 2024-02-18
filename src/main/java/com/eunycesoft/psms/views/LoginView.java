package com.eunycesoft.psms.views;

import com.eunycesoft.psms.Constants;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Locale;

import static com.eunycesoft.psms.Application.getCookie;
import static com.eunycesoft.psms.Application.setUserLocale;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {
    public LoginView() {
        setAction("login");

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("PSMS");
        i18n.getHeader().setDescription("Primary School Management System");
        i18n.setAdditionalInformation(null);
        setI18n(i18n);

        setForgotPasswordButtonVisible(false);
        setOpened(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        var langCookie = getCookie(Constants.COOKIE_UI_LANGUAGE);
        if (langCookie == null) setUserLocale(Locale.ENGLISH);
        else setUserLocale(new Locale(langCookie.getValue()));
        if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            this.setError(true);
        }
    }
}
