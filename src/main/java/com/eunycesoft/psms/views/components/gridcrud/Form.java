package com.eunycesoft.psms.views.components.gridcrud;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.util.List;


@Getter
public class Form extends VerticalLayout {

    protected Span caption = new Span();
    protected HorizontalLayout header = new HorizontalLayout(caption);
    protected FormLayout content = new FormLayout();
    protected Button cancelButton = new Button("Cancel", evt -> onCancel());
    protected Button submitButton = new Button("OK", evt -> onSubmit());
    protected HorizontalLayout footer = new HorizontalLayout(cancelButton, submitButton);

    public Form() {
        super();
        var scroller = new Scroller(content, Scroller.ScrollDirection.VERTICAL);
        add(header, new Hr(), scroller, new Hr(), footer);

        setMargin(false);
        setSpacing(false);
        setPadding(false);
        setSizeFull();

        caption.setWidthFull();
        caption.getStyle().set("color", "var(--lumo-primary-text-color)")
                .set("font-size", "1.4em");

        List.of(header, footer).forEach(elt -> {
            elt.setWidthFull();
            elt.setMargin(false);
            elt.setPadding(true);
        });
//        header.addClassName("header");
//        footer.addClassName("footer");

        scroller.setSizeFull();
        content.setResponsiveSteps(new ResponsiveStep("0px", 1),
                new ResponsiveStep("550px", 2));
        content.getStyle().set("padding", "0px 10px");
        setFlexGrow(1, scroller);

        footer.setJustifyContentMode(JustifyContentMode.BETWEEN);
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClickShortcut(Key.ENTER);
    }


    public void setCaptionFontSize(String fontSize) {
        caption.getStyle().remove("font-size").set("font-size", fontSize);
    }

    public void setTitle(String title) {
        caption.setText(title);
    }

    public void show(HasComponents parent) {
        parent.add(this);
    }

    public void show() {
        var dialog = new Dialog(this);
        dialog.setDraggable(true);
        dialog.setModal(true);
        dialog.setResizable(true);
        dialog.setCloseOnOutsideClick(false);
        dialog.setWidth("400px");
        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.open();
    }

    public void close() {
        if (isAttached()) {
            var parent = getParent().get();
            if (parent instanceof Dialog) ((Dialog) parent).close();
            else parent.getElement().removeChild(this.getElement());
        }
    }

    public void onSubmit() {
        fireEvent(new FormSubmitEvent(this, true));
        close();
    }

    public void onCancel() {
        close();
    }

    public Registration addFormSubmitListener(ComponentEventListener<FormSubmitEvent> listener) {
        return addListener(FormSubmitEvent.class, listener);
    }

    @Getter
    public static class FormSubmitEvent extends ComponentEvent<Form> {
        public FormSubmitEvent(Form source, boolean fromClient) {
            super(source, fromClient);
        }
    }
}
