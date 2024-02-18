package com.eunycesoft.psms.report;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.enums.Section;
import com.eunycesoft.psms.data.repository.StudentRepository;
import com.vaadin.flow.shared.util.SharedUtil;
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.constant.GroupHeaderLayout;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.definition.ReportParameters;

import java.util.*;

import static com.eunycesoft.psms.Constants.dateFormatter;
import static net.sf.dynamicreports.report.builder.DynamicReports.*;


public class StudentsListBuilder extends BaseReportBuilder {
    protected StudentsListBuilder(Optional<Section> section, Optional<Classroom> classroom, String lang) {
        super(PageType.A4, PageOrientation.LANDSCAPE);
        var repo = (StudentRepository) Application.repositories.getRepositoryFor(Student.class).get();
        List<Student> list;
        if (classroom.isPresent())
            list = classroom.get().getStudents();
        else if (section.isPresent())
            list = section.get().getStudents();
        else list = repo.findAll();
        list.sort(Comparator.<Student>comparingInt(sdt -> sdt.getClassroom().getId())
                .thenComparing(Student::getFullName));
        var ds = new DRDataSource("className", "registrationNumber", "fullName", "gender", "dateOfBirth", "placeOfBirth", "fatherPhone", "motherPhone");
        list.forEach(sdt -> {
            ds.add(sdt.getClassroom().getName(),
                    sdt.getRegistrationNumber(),
                    sdt.getFullName(),
                    sdt.getGender().name(),
                    (sdt.getDateOfBirth() == null) ? "" : dateFormatter.format(sdt.getDateOfBirth()),
                    (sdt.getPlaceOfBirth() == null) ? "" : SharedUtil.capitalize(sdt.getPlaceOfBirth().toLowerCase()),
                    Objects.toString(sdt.getFatherPhone(), ""),
                    Objects.toString(sdt.getMotherPhone(), ""));
        });
        dataSource = ds;
        locale = Locale.forLanguageTag(lang);
    }

    @Override
    public HorizontalListBuilder createHeader() {
        var groupHeaderExpression = new AbstractSimpleExpression<String>() {
            @Override
            public String evaluate(ReportParameters params) {
                var classroom = params.getFieldValue("className");
                return params.getMessage("SL.header_informations", new Object[]{classroom});
            }
        };
        return super.createHeader().newRow().add(cmp.text(groupHeaderExpression).setHeight(30).setStyle(Styles.boldCenteredStyle));
    }

    @Override
    public BaseReportBuilder buildReport() {
        super.buildReport();

        titleField.setText(exp.message("SL.title"));

        var groupByClass = grp.group("className", String.class)
                .setHeaderLayout(GroupHeaderLayout.EMPTY)
                .setPadding(0)
                .resetPageNumber()
                .startInNewPage();

        columnsWidths = new int[]{20, 90, 230, 40, 70, 70, 70, 70, 60, 60};
        columnsTitleKeys = new String[]{null, "shared.reg_number", "shared.student_name", "shared.gender", "shared.born_on",
                "shared.born_at", "SL.father_phone", "SL.mother_phone", null, null};
        columnsBuilders = List.of(
                col.column(exp.groupRowNumber(groupByClass)).setPattern("00").setTitle("N<sup>o</sup>"),
                col.column("registrationNumber", type.stringType()),
                col.column("fullName", type.stringType()),
                col.column("gender", type.stringType()),
                col.column("dateOfBirth", type.stringType()),
                col.column("placeOfBirth", type.stringType()),
                col.column("fatherPhone", type.stringType()),
                col.column("motherPhone", type.stringType()),
                col.column(exp.text("")).setTitle(""),
                col.column(exp.text("")).setTitle("")
        );

        configureColumnsBuilders(columnsWidths, columnsTitleKeys, columnsBuilders);
        columnsBuilders.get(2).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
        pageHeader(createHeader().setPrintWhenExpression(exp.printInFirstPage()))
                .groupBy(groupByClass)
                .groupFooter(groupByClass, createDoneAtOn().add(createHeadMasterOnlySignature()))
                .columns(columnsBuilders.toArray(new TextColumnBuilder[0]));
        return this;
    }

}
