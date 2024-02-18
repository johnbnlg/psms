package com.eunycesoft.psms.views;

import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.enums.Language;
import com.eunycesoft.psms.data.enums.Period;
import com.eunycesoft.psms.data.enums.Section;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;
import java.util.List;

import static com.eunycesoft.psms.Constants.*;
import static com.eunycesoft.psms.data.enums.Section.ANG;
import static com.eunycesoft.psms.data.enums.Section.BIL;

@PageTitle("Students periodic results")
@Route(value = "PeriodicResultsView", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class PeriodicResultsView extends VerticalLayout {
    private final ComboBox<Period> cboxPeriod = new ComboBox<>("Period", Period.getTerms());
    private final ComboBox<Section> cboxSection = new ComboBox<>("Section", Section.values());
    private final ComboBox<Classroom> cboxClassroom = new ComboBox<>("Classroom");
    private final ComboBox<Language> cboxLanguage = new ComboBox<>("Language", Language.values());
    private final Button cmdViewReport = new Button("Report cards");
    private final Button cmdViewResultSheet = new Button("Result summary");
    private final Button cmdViewMarkSheet = new Button("Marks sheet");
    private final Button cmdViewTopN = new Button("Classes top n students");
    private final Button cmdViewTop10 = new Button("School top 10 students");
    private final Button cmdViewLast10 = new Button("School last 10 students");
    private final Button cmdViewClassesRanking = new Button("Classes ranking");


    public PeriodicResultsView() {

        buildUi();
        buildLogic();

        cmdViewReport.addClickListener(e -> {
            if (cboxPeriod.getValue() != null && cboxSection.getValue() != null && cboxClassroom.getValue() != null && cboxLanguage.getValue() != null) {
                UI.getCurrent().getPage().open(String.format(TERM_CLASS_REPORT_CARD,
                        cboxPeriod.getValue().name(), cboxClassroom.getValue().name(), cboxLanguage.getValue().name()));
            }
        });

        cmdViewResultSheet.addClickListener(e -> {
            if (cboxPeriod.getValue() != null && cboxSection.getValue() != null && cboxClassroom.getValue() != null && cboxLanguage.getValue() != null) {
                UI.getCurrent().getPage().open(String.format(CLASSROOM_RESULT_SHEET_URL_PATTERN,
                        cboxPeriod.getValue().name(), cboxClassroom.getValue().name(), cboxLanguage.getValue().name()));
            }
        });

        cmdViewMarkSheet.addClickListener(e -> {
            var request = String.format(TERM_MARK_SHEET,
                    cboxPeriod.getValue().name(), cboxClassroom.getValue().name(), cboxLanguage.getValue().name());
            UI.getCurrent().getPage().open(request);
        });


        cmdViewTopN.addClickListener(e -> {

            if (cboxPeriod.getValue() != null) {
                var input = new IntegerField();
                input.setValue(3);
                var diag = new Dialog();
                diag.add(new Text("Valeur de N:"), input, new Button("OK", evt -> {
                    var number = input.getValue();
                    if (number == null) number = 3;
                    UI.getCurrent().getPage().open(String.format(TERM_CLASS_TOP_N_STUDENTS,
                            cboxPeriod.getValue().name(), number));
                    diag.close();
                }));
                diag.open();

            }
        });
    }

    private void buildUi() {

        setAlignItems(Alignment.CENTER);
        List.of(cmdViewMarkSheet, cmdViewReport, cmdViewResultSheet, cmdViewTopN, cmdViewTop10, cmdViewLast10, cmdViewClassesRanking)
                .forEach(btn -> {
                    btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                    btn.setEnabled(false);
                });

        setSpacing(false);
        var form = new FormLayout(cboxPeriod, cboxSection, cboxClassroom, cboxLanguage, cmdViewMarkSheet,
                cmdViewResultSheet, cmdViewReport, cmdViewClassesRanking, cmdViewTopN, cmdViewTop10, cmdViewLast10);
        form.setResponsiveSteps(new ResponsiveStep("0em", 1));
        form.setWidth("350px");
        add(form);
        setJustifyContentMode(JustifyContentMode.CENTER);

    }

    private void buildLogic() {
        cboxPeriod.addValueChangeListener(evt -> {
            cmdViewTopN.setEnabled(evt.getValue() != null);
            cmdViewLast10.setEnabled(evt.getValue() != null);
            cmdViewTop10.setEnabled(evt.getValue() != null);
        });

        cboxSection.addValueChangeListener(evt -> {
            cboxClassroom.setItems(evt.getValue().getClassrooms());
            if (evt.getValue() == ANG) {
                cboxLanguage.setValue(Language.EN);
            } else {
                cboxLanguage.setValue(Language.FR);
            }
            cboxLanguage.setEnabled(evt.getValue() == BIL);
        });

        cboxClassroom.addValueChangeListener(evt -> {
            List.of(cmdViewMarkSheet, cmdViewReport, cmdViewResultSheet, cmdViewClassesRanking).forEach(btn -> {
                btn.setEnabled(evt.getValue() != null);
            });
        });
    }


}
