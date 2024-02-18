package com.eunycesoft.psms.views.components.gridcrud;

import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.Getter;

@Getter
public enum CrudOperation {
    CREATE("Adding a new %s", "Add", VaadinIcon.PLUS, "%s saved successfully", "Failed to save the new %s. Cause: %s"),
    READ("Details on selected %s", "OK", VaadinIcon.THUMBS_UP, "%d %s(s) found", "Failed to load %s(s). Cause: %s"),
    UPDATE("Updating the selected %s", "Update", VaadinIcon.CHECK, "%s updated successfully", "Failed to update the new %s. Cause: %s"),
    DELETE("Delete the selected %s?", "Yes, delete", VaadinIcon.TRASH, "%s deleted successfully", "Failed to delete the new %s. Cause: %s");

    private final String captionFormat;
    private final String submitButtonText;
    private final VaadinIcon icon;
    private final String successMassageFormat;
    private final String errorMassageFormat;

    CrudOperation(String captionFormat, String submitButtonText, VaadinIcon icon, String successMassageFormat, String errorMassageFormat) {
        this.captionFormat = captionFormat;
        this.submitButtonText = submitButtonText;
        this.icon = icon;
        this.successMassageFormat = successMassageFormat;
        this.errorMassageFormat = errorMassageFormat;
    }
}
