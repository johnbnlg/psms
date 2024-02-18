package com.eunycesoft.psms.views.components;

import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.enums.Fee;
import com.eunycesoft.psms.data.enums.FinancialReportGranularity;
import com.eunycesoft.psms.views.components.gridcrud.Form;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Hr;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.eunycesoft.psms.Constants.FINANCIAL_REPORT_URL_PATTERN;
import static com.eunycesoft.psms.data.enums.Fee.*;

public class FinancialReportPrintDialog extends Form {

    private ComboBox<FinancialReportGranularity> granularitySelect = new ComboBox("Choose the report granularity", FinancialReportGranularity.values());
    private CheckboxGroup<Fee> feeSelect = new CheckboxGroup<>("Check the concerned fees", values());
    private Checkbox selectOrDeselectAll = new Checkbox("Select/Deselect all fees", true, evt -> {
        if (evt.getValue())
            feeSelect.select(feeSelect.getListDataView().getItems().collect(Collectors.toList()));
        else feeSelect.deselectAll();
    });

    public FinancialReportPrintDialog() {
        setTitle("Print financial report");
        getContent().add(granularitySelect, feeSelect, new Hr(), selectOrDeselectAll);
        granularitySelect.setPlaceholder("Choose the granularity");
        granularitySelect.setItemLabelGenerator(item -> Utils.toHumanFriendly(item.name()));
        granularitySelect.setValue(FinancialReportGranularity.STUDENT);
        feeSelect.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        feeSelect.select(List.of(REG, SLIDE1, SLIDE2));
    }

    @Override
    public void onSubmit() {
        var fees = StringUtils.join(feeSelect.getSelectedItems().stream().map(Fee::name).collect(Collectors.toList()), ",");
        Utils.openLinkOnNewTab(FINANCIAL_REPORT_URL_PATTERN, granularitySelect.getValue(), fees);
        super.onSubmit();
    }
}
