package com.eunycesoft.psms.views.components;

import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.enums.Section;
import com.eunycesoft.psms.data.enums.StudentsListGranularity;
import com.eunycesoft.psms.views.components.gridcrud.Form;
import com.vaadin.flow.component.combobox.ComboBox;

import static com.eunycesoft.psms.Constants.*;

public class StudentListPrintDialog extends Form {
    private ComboBox<StudentsListGranularity> granularitySelect = new ComboBox("Print the list for", StudentsListGranularity.values());
    private ComboBox<Section> sectionSelect = new ComboBox("Choose the concerned section", Section.values());
    private ComboBox<Classroom> classroomSelect = new ComboBox("Choose the concerned classroom");

    public StudentListPrintDialog() {
        setTitle("Print students list");
        getContent().add(granularitySelect, sectionSelect, classroomSelect);
        granularitySelect.setPlaceholder("List for");
        classroomSelect.setPlaceholder("Section");
        classroomSelect.setPlaceholder("Classroom");
        sectionSelect.setVisible(false);
        classroomSelect.setVisible(false);
        granularitySelect.addValueChangeListener(evt -> {
            sectionSelect.setVisible(evt.getValue() != null
                                     && (evt.getValue().equals(StudentsListGranularity.SECTION) || evt.getValue().equals(StudentsListGranularity.CLASSROOM)));
            classroomSelect.setVisible(evt.getValue() != null && evt.getValue().equals(StudentsListGranularity.CLASSROOM));
        });

        sectionSelect.addValueChangeListener(evt -> {
            if (evt.getValue() != null) {
                classroomSelect.setItems(evt.getValue().getClassrooms());
            }
        });
    }

    @Override
    public void onSubmit() {
        switch (granularitySelect.getValue()) {
            case GLOBAL -> Utils.openLinkOnNewTab(GLOBAL_STUDENTS_LIST_URL_PATTERN);
            case SECTION -> {
                if (sectionSelect.getValue() != null)
                    Utils.openLinkOnNewTab(SECTION_STUDENTS_LIST_URL_PATTERN, sectionSelect.getValue().name());
            }
            case CLASSROOM -> {
                if (classroomSelect.getValue() != null)
                    Utils.openLinkOnNewTab(CLASSROOM_STUDENTS_LIST_URL_PATTERN, classroomSelect.getValue().name());
            }
        }
        super.onSubmit();
    }
}
