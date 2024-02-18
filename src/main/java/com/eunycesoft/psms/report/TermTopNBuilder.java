package com.eunycesoft.psms.report;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.Constants;
import com.eunycesoft.psms.data.entity.StudentResultByLanguage;
import com.eunycesoft.psms.data.enums.*;
import com.eunycesoft.psms.data.repository.StudentResultByLanguageRepository;
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.constant.GroupHeaderLayout;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.List;
import java.util.Locale;

import static com.eunycesoft.psms.Constants.averagePattern;
import static net.sf.dynamicreports.report.builder.DynamicReports.*;


public class TermTopNBuilder extends BaseReportBuilder {

    private int number;

    private Period period;

    protected TermTopNBuilder(Period period, Integer number, String lang) {
        super(PageType.A4, PageOrientation.LANDSCAPE);
        this.number = number;
        this.period = period;
        var repo = (StudentResultByLanguageRepository) Application.repositories.getRepositoryFor(StudentResultByLanguage.class).get();
        dataSource = new JRBeanCollectionDataSource(repo.findByLanguage(Language.valueOf(lang.toUpperCase()))
                .stream().filter(rs->rs.getTerm3Average() > 0).toList());
        locale = Locale.forLanguageTag(lang);
    }

    @Override
    public HorizontalListBuilder createHeader() {
        var groupHeaderExpression = new AbstractSimpleExpression<String>() {
            @Override
            public String evaluate(ReportParameters params) {
                var classroom = params.getFieldValue("student.classroom.name");
                return params.getMessage("shared.class_prompt", new Object[]{classroom});
            }
        };
        return super.createHeader().newRow().add(cmp.text(groupHeaderExpression).setHeight(30)
                .setStyle(stl.style(Styles.boldCenteredStyle).setFontSize(20)));
    }

    @Override
    public BaseReportBuilder buildReport() {
        super.buildReport();

        titleField.setText(exp.message("SDTR.term_class_top_n_title", new Object[]{number, Constants.CURRENT_SCHOOL_YEAR}));

        var sectionGroup = grp.group("student.classroom.section", Section.class)
                .setHeaderLayout(GroupHeaderLayout.EMPTY)
                .resetPageNumber()
                .startInNewPage()
                .setPadding(0);
        var classGroup = grp.group("student.classroom", Classroom.class)
                .setHeaderLayout(GroupHeaderLayout.EMPTY)
                .setPadding(0);

        columnsWidths = new int[]{1, 10, 2, 2, 3};
        columnsTitleKeys = new String[]{null, "shared.student_name", "shared.gender", "RC.globalAvg", null};
        TextColumnBuilder<?> studentName, classroom, gender, section, avg;
        columnsBuilders = List.of(
                col.column(exp.groupRowNumber(classGroup)).setPattern("00").setTitle("N<sup>o</sup>"),
                studentName = col.column("student.fullName", String.class),
                gender = col.column("student.gender", Gender.class),
                avg = col.column("term3Average", Double.class).setPattern(averagePattern),
                col.column(exp.text("")).setTitle("Observation")
        );
        configureColumnsBuilders(columnsWidths, columnsTitleKeys, columnsBuilders);
        columnsBuilders.get(1).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

        pageHeader(createHeader().newRow(5)
                .add(cmp.horizontalList(
                        cmp.text("SECTION ").setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT).setStyle(Styles.boldStyle),
                        cmp.text(field("student.classroom.section.nameFr", String.class)).setStyle(Styles.boldStyle)
                )).newRow(5).setPrintWhenExpression(exp.printInFirstPage()))
                .groupBy(sectionGroup, classGroup)
                .groupHeader(classGroup, cmp.text(field("student.classroom.name", String.class)).setStyle(Styles.boldCenteredStyle))
                .groupFooter(classGroup, cmp.verticalGap(5))
                .groupFooter(sectionGroup, createDoneAtOn().add(createHeadMasterOnlySignature()))
                .columns(columnsBuilders.toArray(new TextColumnBuilder[0]));

        return this;
    }

}
