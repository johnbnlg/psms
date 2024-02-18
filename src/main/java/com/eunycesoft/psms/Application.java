package com.eunycesoft.psms;

import com.eunycesoft.psms.data.ExtendedJpaRepositoryImpl;
import com.eunycesoft.psms.security.AuthenticatedUser;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.support.Repositories;

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.util.Locale;

/**
 * The entry point of the Spring Boot application.
 * <p>
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = ExtendedJpaRepositoryImpl.class)
@Theme(value = "psms")
@PWA(name = "psms", shortName = "psms", offlineResources = {})
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {
    public static Repositories repositories;
    public static AuthenticatedUser authenticatedUser;

//    @Value("${psms.fee.dateline.registration}")
//    public static LocalDate regDateline;
//    @Value("${psms.fee.dateline.slide1}")
//    public static LocalDate slide1Dateline;
//    @Value("${psms.fee.dateline.slide2}")
//    public static LocalDate slide2Dateline;
//    @Value("${psms.fee.dateline.transport}")
//    public static LocalDate transportDateline;
//    @Value("${psms.fee.dateline.cep}")
//    public static LocalDate cepDateline;
//    @Value("${psms.fee.dateline.fslc}")
//    public static LocalDate fslcDateline;
//    @Value("${psms.fee.dateline.concours}")
//    public static LocalDate entSixDateline;
//    @Value("${psms.fee.dateline.common}")
//    public static LocalDate comEntDateline;

    public static void main(String[] args) {
        var context = SpringApplication.run(Application.class, args);
    }

    public static Cookie getCookie(String name) {
        Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    public static void setCookie(String name, String value) {
        var cookie = getCookie(name);
        if (cookie != null) {
            cookie.setValue(value);
        } else {
            cookie = new Cookie(name, value);
            cookie.setComment("Users's ui language");
        }
        cookie.setMaxAge(365 * 24 * 60 * 60);
        cookie.setPath(VaadinService.getCurrentRequest().getContextPath());
        VaadinService.getCurrentResponse().addCookie(cookie);
    }

    public static void setUserLocale(Locale locale) {
        VaadinSession.getCurrent().setLocale(locale);
        setCookie(Constants.COOKIE_UI_LANGUAGE, locale.getLanguage());
    }
}
