package com.eunycesoft.psms.views;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.entity.User;
import com.eunycesoft.psms.security.AuthenticatedUser;
import com.eunycesoft.psms.views.crud.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import lombok.Getter;

import java.util.Locale;
import java.util.Optional;

import static com.eunycesoft.psms.Constants.COOKIE_UI_LANGUAGE;
import static com.eunycesoft.psms.Constants.defaultLocale;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {
    private H1 viewTitle;
    private RadioButtonGroup<Locale> langRadio = new RadioButtonGroup<>();
    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        Application.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("view-toggle");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.setWidthFull();
        viewTitle.addClassNames("view-title");

        langRadio.setItems(Locale.ENGLISH, Locale.FRENCH);
        langRadio.setWidth("150px");
        langRadio.setRenderer(new TextRenderer<>(item -> item.getLanguage().toUpperCase()));
        if (Application.getCookie(COOKIE_UI_LANGUAGE) == null)
            langRadio.setValue(defaultLocale);
        else langRadio.setValue(Locale.forLanguageTag(Application.getCookie(COOKIE_UI_LANGUAGE).getValue()));
        langRadio.addValueChangeListener(e -> {
            Application.setUserLocale(e.getValue());
            UI.getCurrent().getPage().reload();
        });

        Header header = new Header(toggle, viewTitle, langRadio);
        header.addClassNames("view-header");
//        header.getElement().getThemeList().set("dark", true);
        return header;
    }

    private Component createDrawerContent() {
        var logo = new Image("images/drawer-logo.png", "sms logo");
        var section = new com.vaadin.flow.component.html.Section(logo, new Hr(), createNavigation(), createFooter());
        section.addClassNames("drawer-section");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("menu-item-container");
        nav.getElement().setAttribute("aria-labelledby", "views");

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames("navigation-list");
        nav.add(list);
        for (MenuItemInfo menuItem : createMenuItems()) {
            if (accessChecker.hasAccess(menuItem.getView())) {
                list.add(menuItem);
            }
        }
        return nav;
    }

    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[]{ //
                new MenuItemInfo("Home", VaadinIcon.HOME_O, HomeView.class), //
                new MenuItemInfo("Edit student marks", VaadinIcon.PENCIL, StudentMarkEditView.class),
                new MenuItemInfo("Buser dashboard", VaadinIcon.MONEY, BurserDashboardView.class),
                new MenuItemInfo("Periodic results", VaadinIcon.DIPLOMA, PeriodicResultsView.class),

                // crud menu
 new MenuItemInfo("Appreciation", VaadinIcon.CARET_RIGHT, AppreciationView.class),
 new MenuItemInfo("ClassroomFee", VaadinIcon.CARET_RIGHT, ClassroomFeeView.class),
 new MenuItemInfo("ClassroomSubject", VaadinIcon.CARET_RIGHT, ClassroomSubjectView.class),
 new MenuItemInfo("ClassroomTeacher", VaadinIcon.CARET_RIGHT, ClassroomTeacherView.class),
 new MenuItemInfo("Personnel", VaadinIcon.CARET_RIGHT, PersonnelView.class),
 new MenuItemInfo("Student", VaadinIcon.CARET_RIGHT, StudentView.class),
 new MenuItemInfo("StudentFee", VaadinIcon.CARET_RIGHT, StudentFeeView.class),
 new MenuItemInfo("StudentMark", VaadinIcon.CARET_RIGHT, StudentMarkView.class),
 new MenuItemInfo("StudentPayment", VaadinIcon.CARET_RIGHT, StudentPaymentView.class),
 new MenuItemInfo("StudentResult", VaadinIcon.CARET_RIGHT, StudentResultView.class),
 new MenuItemInfo("StudentResultByGroup", VaadinIcon.CARET_RIGHT, StudentResultByGroupView.class),
 new MenuItemInfo("StudentResultByLanguage", VaadinIcon.CARET_RIGHT, StudentResultByLanguageView.class),
 new MenuItemInfo("StudentResultBySubject", VaadinIcon.CARET_RIGHT, StudentResultBySubjectView.class),
 new MenuItemInfo("User", VaadinIcon.CARET_RIGHT, UserView.class)
// crud menu
        };
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("footer");

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getFullName());
            avatar.setImageResource(Utils.getPhotoAsStreamResource(user));
            avatar.addClassNames("me-xs");

            ContextMenu userMenu = new ContextMenu(layout);
            userMenu.setOpenOnClick(true);
            userMenu.addItem("Profile", e -> {
                UI.getCurrent().navigate(UserInfoEditView.class);
            });
            userMenu.addItem("Logout", e -> {
                authenticatedUser.logout();
            });

            Span name = new Span(user.getFullName());
            name.addClassNames("font-medium", "text-s", "text-secondary");

            layout.add(avatar, name);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }
        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : Utils.toHumanFriendly(title.value());
    }

    /**
     * A simple navigation item component, based on ListItem element.
     */
    public static class MenuItemInfo extends ListItem {
        @Getter
        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, VaadinIcon icon, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            link.addClassNames("menu-item-link");
            link.setRoute(view);
            Span text = new Span(Utils.toHumanFriendly(menuTitle));
            text.addClassNames("menu-item-text");
            var iconComponent = icon.create();
            iconComponent.addClassNames("text-l", "pr-s");
            link.add(iconComponent, text);
            add(link);
        }
    }
}
