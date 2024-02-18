package com.eunycesoft.psms.views.components.wizard;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;


public class WizardPage extends VerticalLayout {
    private Supplier<Boolean> validator = () -> true;
    @Getter
    @Setter
    private String title = "";

    public WizardPage(String title) {
        this.title = title;
        setSizeFull();
    }

    public boolean validatePage() {
        return validator.get();
    }
}
