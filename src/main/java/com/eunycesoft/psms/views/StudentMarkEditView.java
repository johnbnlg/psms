package com.eunycesoft.psms.views;

import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.entity.ClassroomSubject;
import com.eunycesoft.psms.data.entity.StudentMark;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.enums.Language;
import com.eunycesoft.psms.data.enums.Period;
import com.eunycesoft.psms.data.enums.Section;
import com.eunycesoft.psms.data.repository.StudentMarkRepository;
import com.eunycesoft.psms.data.repository.StudentRepository;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.eunycesoft.psms.Constants.markFormat;

@PageTitle("Students marks edit")
@Route(value = "StudentMarkEditView", layout = MainLayout.class)
@PermitAll
public class StudentMarkEditView extends VerticalLayout {

    private final ComboBox<Period> cboxPeriod = new ComboBox<>("", Period.getMonths());
    private final ComboBox<Section> cboxSection = new ComboBox<>("", Section.values());
    private final ComboBox<Classroom> cboxClassroom = new ComboBox<>();
    private final ComboBox<Language> cboxLanguage = new ComboBox<>("", Language.values());
    private final ComboBox<ClassroomSubject> cboxClassroomSubject = new ComboBox<>();
    private final GridPro<StudentMark> grid = new GridPro<>(StudentMark.class);
    private final StudentMarkRepository smRepository;
    private final StudentRepository sdtRepository;
    private final Checkbox toggleNumber = new Checkbox("Ordinal numbers");
    private final Checkbox markByCompetences = new Checkbox("By Competences");
    private Column<StudentMark> numberCol, studentCol, markCol, oralCol, writtenCol, practicalCol, attitudeCol;

    private Double markedOver, oralOver, writtenOver, practicalOver, attitudeOver;
    private DecimalFormat formatter = new DecimalFormat("#.#");

    @Autowired
    public StudentMarkEditView(StudentMarkRepository smRepository,
                               StudentRepository sdtRepository) {
        this.smRepository = smRepository;
        this.sdtRepository = sdtRepository;
    }

    @PostConstruct
    public void init() {
        buildUi();
        buildLogic();
    }

    private void buildUi() {
        Map.of(cboxPeriod, "Period",
                cboxSection, "Section",
                cboxClassroom, "Classroom",
                cboxLanguage, "Language",
                cboxClassroomSubject, "Subject"
        ).forEach((cbox, placeholder) -> cbox.setPlaceholder(placeholder));

        List.of(cboxPeriod, cboxSection, cboxClassroom, cboxLanguage, cboxClassroomSubject).forEach(cbox -> {
            cbox.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
            cbox.setAllowCustomValue(false);
        });
        cboxLanguage.setValue(Language.FR);
        cboxClassroomSubject.setItemLabelGenerator(cs->cs.toString(cboxLanguage.getValue()));

        grid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setPageSize(100);
        grid.setWidthFull();
        grid.setEnterNextRow(true);
        grid.setEditOnClick(true);
        grid.removeAllColumns();
        numberCol = grid.addEditColumn("student.number").text(this::updateNumber).setHeader("Num");
        studentCol = grid.addColumn("student").setWidth("400px").setFlexGrow(0);
        markCol = grid.addEditColumn(StudentMark::getMark, new NumberRenderer<>(StudentMark::getMark, markFormat)).text(this::updateMark).setHeader("Mark").setKey("Mark");
        oralCol = grid.addEditColumn(StudentMark::getOral, new NumberRenderer<>(StudentMark::getOral, markFormat)).text(this::updateOral).setHeader("Oral").setKey("Oral");
        writtenCol = grid.addEditColumn(StudentMark::getWritten, new NumberRenderer<>(StudentMark::getWritten, markFormat)).text(this::updateWritten).setHeader("Written").setKey("Written");
        practicalCol = grid.addEditColumn(StudentMark::getPractical, new NumberRenderer<>(StudentMark::getPractical, markFormat)).text(this::updatePractical).setHeader("Practical").setKey("Practical");
        attitudeCol = grid.addEditColumn(StudentMark::getAttitude, new NumberRenderer<>(StudentMark::getAttitude, markFormat)).text(this::updateAttitude).setHeader("Attitude").setKey("Attitude");
        grid.addColumn(sm -> "").setAutoWidth(true);
        List.of(numberCol, oralCol, writtenCol, practicalCol, attitudeCol).forEach(col -> col.setVisible(false));
        grid.getColumns().forEach(col -> col.setSortable(true).setResizable(true));
        List.of(numberCol, markCol, oralCol, writtenCol, practicalCol, attitudeCol)
                .forEach(col -> col.setWidth("90px").setFlexGrow(0));
        var form = new FormLayout(cboxPeriod, cboxSection, cboxClassroom, cboxLanguage, cboxClassroomSubject, toggleNumber, markByCompetences);
        form.setResponsiveSteps(new ResponsiveStep("0px", 1), new ResponsiveStep("600px", 3));
        form.setWidth("650px");
        form.setColspan(cboxClassroomSubject, 2);
        form.setColspan(markByCompetences, 2);
        add(form, grid);
        setSpacing(false);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
    }

    private void buildLogic() {
        cboxPeriod.addValueChangeListener(evt -> {
            if (cboxClassroomSubject.getValue() != null)
                refreshGrid();
        });

        cboxSection.addValueChangeListener(evt -> {
            cboxClassroomSubject.setItems(new ArrayList<>());
            if (evt.getValue() != null){
                cboxClassroom.setItems(evt.getValue().getClassrooms());
                switch (evt.getValue()) {
                    case FRA, BIL -> cboxLanguage.setValue(Language.FR);
                    case ANG -> cboxLanguage.setValue(Language.EN);
                }
            }
        });

        cboxClassroom.addValueChangeListener(evt -> {
            if (evt.getValue() == null)cboxClassroomSubject.setItems(new ArrayList<>());
            else cboxClassroomSubject.setItems(evt.getValue().getSubjects(cboxLanguage.getValue()));
        });
        cboxLanguage.addValueChangeListener(evt -> {
            if (cboxClassroom.getValue() != null)
                cboxClassroomSubject.setItems(cboxClassroom.getValue().getSubjects(cboxLanguage.getValue()));
            else cboxClassroomSubject.setItems(new ArrayList<>());
        });
        cboxClassroomSubject.addValueChangeListener(evt -> {
            if (evt.getValue() == null) {
                grid.setItems(new ArrayList<>());
                return;
            }
            markedOver = evt.getValue().getMarkedOver();
            oralOver = markedOver * evt.getValue().getOralRatio();
            writtenOver = markedOver * evt.getValue().getWrittenRatio();
            practicalOver = markedOver * evt.getValue().getPracticalRatio();
            attitudeOver = markedOver * evt.getValue().getAttitudeRatio();
            var competences = new StringBuilder();
            Map.of(attitudeCol, attitudeOver, practicalCol, practicalOver, writtenCol, writtenOver, oralCol, oralOver).forEach((col, over) -> {
                if (over != null) {
                    col.setVisible(markByCompetences.getValue());
                    competences.append(col.getKey() + ":" + formatter.format(over) + "  ");
                } else col.setVisible(false);
            });
            cboxClassroomSubject.setHelperText(competences.toString());
            refreshGrid();
        });

        grid.getListDataView().addItemCountChangeListener(evt -> {
            studentCol.setFooter("Students count: " + evt.getItemCount());
        });

        toggleNumber.addValueChangeListener(evt -> {
            numberCol.setVisible(evt.getValue());
        });
        markByCompetences.addValueChangeListener(evt -> {
            markCol.setVisible(evt.getOldValue());
            Map.of(oralCol, oralOver, writtenCol, writtenOver, practicalCol, practicalOver, attitudeCol, attitudeOver).forEach((col, over) -> {
                if (over != null) col.setVisible(evt.getValue());
            });
        });
    }

    public void updateMark(StudentMark sm, String str) {
        Double mark, max = markedOver;
        try {
            mark = str.isBlank() ? null : Double.valueOf(str);
            if ((mark == null) || (mark >= 0 && mark <= max)) {
                sm.setMark(mark);
                sm.splitMark(mark);
                smRepository.save(sm);
            } else throw new NumberFormatException();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Utils.showErrorNotification("Please enter a valid mark between 0 and %s", formatter.format(max));
        }
    }

    public void updateOral(StudentMark sm, String str) {
        Double mark, max = oralOver;
        try {
            mark = str.isBlank() ? null : Double.valueOf(str);
            if ((mark == null) || (mark >= 0 && mark <= max)) {
                sm.setOral(mark);
                sm.setMark(Utils.doubleSum(sm.getOral(), sm.getWritten(), sm.getPractical(), sm.getAttitude()));
                smRepository.save(sm);
            } else throw new NumberFormatException();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Utils.showErrorNotification("Please enter a valid oral mark between 0.0 and %s", formatter.format(max));
        }
    }

    public void updateWritten(StudentMark sm, String str) {
        Double mark, max = writtenOver;
        try {
            mark = str.isBlank() ? null : Double.valueOf(str);
            if ((mark == null) || (mark >= 0 && mark <= max)) {
                sm.setWritten(mark);
                sm.setMark(Utils.doubleSum(sm.getOral(), sm.getWritten(), sm.getPractical(), sm.getAttitude()));
                smRepository.save(sm);
            } else throw new NumberFormatException();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Utils.showErrorNotification("Please enter a valid written mark between 0.0 and %s", formatter.format(max));
        }
    }

    public void updatePractical(StudentMark sm, String str) {
        Double mark, max = practicalOver;
        try {
            mark = str.isBlank() ? null : Double.valueOf(str);
            if ((mark == null) || (mark >= 0 && mark <= max)) {
                sm.setPractical(mark);
                sm.setMark(Utils.doubleSum(sm.getOral(), sm.getWritten(), sm.getPractical(), sm.getAttitude()));
                smRepository.save(sm);
            } else throw new NumberFormatException();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Utils.showErrorNotification("Please enter a valid practical mark between 0.0 and %s", formatter.format(max));
        }
    }

    public void updateAttitude(StudentMark sm, String str) {
        Double mark, max = attitudeOver;
        try {
            mark = str.isBlank() ? null : Double.valueOf(str);
            if ((mark == null) || (mark >= 0 && mark <= max)) {
                sm.setAttitude(mark);
                sm.setMark(Utils.doubleSum(sm.getOral(), sm.getWritten(), sm.getPractical(), sm.getAttitude()));
                smRepository.save(sm);
            } else throw new NumberFormatException();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Utils.showErrorNotification("Please enter a valid attitude mark between 0.0 and %s", formatter.format(max));
        }
    }

    public void updateNumber(StudentMark sm, String str) {
        Integer number;
        try {
            number = str.isBlank() ? null : Integer.valueOf(str);
            sm.getStudent().setNumber(number);
            sdtRepository.save(sm.getStudent());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Utils.showErrorNotification("Please enter a valid student number");
        }
    }


    private void refreshGrid() {
        grid.setItems(smRepository.findByPeriodClassroomSubject(
                cboxPeriod.getValue(), cboxClassroomSubject.getValue())
        );
    }
}