package com.eunycesoft.psms.views.components;

import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.data.enums.Fee;
import com.eunycesoft.psms.views.components.gridcrud.Form;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.html.Hr;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.eunycesoft.psms.Constants.STUDENT_RECEIPT_URL_PATTERN;
import static com.eunycesoft.psms.data.enums.Fee.*;

public class StudentReceiptPrintDialog extends Form {
    private final Student student;
    private CheckboxGroup<Fee> feeSelect = new CheckboxGroup<>("Check the concerned fees");
    private Checkbox selectOrDeselectAll = new Checkbox("Select/Deselect all fees", true, evt -> {
        if (evt.getValue())
            feeSelect.select(feeSelect.getListDataView().getItems().collect(Collectors.toList()));
        else feeSelect.deselectAll();
    });

    public StudentReceiptPrintDialog(Student student) {
        this.student = student;
        setTitle("Student's payments summary");
        feeSelect.setItems(student.getFees());
        feeSelect.select(List.of(REG, SLIDE1, SLIDE2));
        feeSelect.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        getContent().add(feeSelect, new Hr(), selectOrDeselectAll);
    }

    @Override
    public void onSubmit() {
        var fees = StringUtils.join(feeSelect.getSelectedItems().stream().map(Fee::name).collect(Collectors.toList()), ",");
        Utils.openLinkOnNewTab(STUDENT_RECEIPT_URL_PATTERN, student.getId(), fees);
        super.onSubmit();
    }
}
