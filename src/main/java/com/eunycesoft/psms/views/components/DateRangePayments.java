package com.eunycesoft.psms.views.components;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.data.entity.StudentPayment;
import com.eunycesoft.psms.data.enums.Fee;
import com.eunycesoft.psms.data.repository.StudentPaymentRepository;
import com.eunycesoft.psms.views.components.gridcrud.Form;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.eunycesoft.psms.data.enums.Fee.*;

public class DateRangePayments extends Form {
    private CheckboxGroup<Fee> feeSelect = new CheckboxGroup<>("Check the concerned fees");
    private Checkbox selectOrDeselectAll = new Checkbox("Select/Deselect all fees", true, evt -> {
        if (evt.getValue())
            feeSelect.select(feeSelect.getListDataView().getItems().collect(Collectors.toList()));
        else feeSelect.deselectAll();
    });

    private DatePicker fromDate = new DatePicker("From", LocalDate.now());
    private DatePicker toDate = new DatePicker("To", LocalDate.now());
    private HorizontalLayout periodLayout = new HorizontalLayout(fromDate, toDate);
    private Span result = new Span();
    private StudentPaymentRepository spRepo = (StudentPaymentRepository) Application.repositories.getRepositoryFor(StudentPayment.class).get();

    public DateRangePayments() {
        setTitle("Payments on date range");
        feeSelect.setItems(Fee.values());
        feeSelect.select(List.of(REG, SLIDE1, SLIDE2));
        feeSelect.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        Stream.of(fromDate, toDate).forEach(datePicker -> {
            datePicker.addThemeVariants(DatePickerVariant.LUMO_SMALL);
            datePicker.setMax(LocalDate.now());
        });
        feeSelect.addValueChangeListener(evt -> {
            updateResult();
        });
        fromDate.addValueChangeListener(evt -> {
            if (toDate.getValue().isBefore(evt.getValue()))
                toDate.setValue(evt.getValue());
            toDate.setMin(evt.getValue());
            updateResult();
        });

        toDate.addValueChangeListener(evt -> {
            if (fromDate.getValue().isAfter(evt.getValue()))
                fromDate.setValue(evt.getValue());
            fromDate.setMax(evt.getValue());
            updateResult();
        });
        getContent().add(selectOrDeselectAll, new Hr(), feeSelect, periodLayout, new Hr(), result);
        updateResult();
    }

    private void updateResult() {
        var amount = spRepo.findByFeeInAndPaymentDateBetween(feeSelect.getSelectedItems(), fromDate.getValue(), toDate.getValue())
                .stream().mapToInt(StudentPayment::getAmount).sum();
        result.setText(String.format("Amount of payments: %,d FCFA", amount));
    }
}
