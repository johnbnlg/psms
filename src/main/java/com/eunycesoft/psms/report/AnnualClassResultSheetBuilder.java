package com.eunycesoft.psms.report;


import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.Constants;
import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.entity.StudentResultByLanguage;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.enums.Language;
import com.eunycesoft.psms.data.enums.Period;
import com.eunycesoft.psms.data.repository.StudentResultByLanguageRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.datasource.DRDataSource;

import java.util.List;
import java.util.Locale;

import static com.eunycesoft.psms.Constants.averagePattern;
import static com.eunycesoft.psms.Utils.rankFormat;
import static net.sf.dynamicreports.report.builder.DynamicReports.*;


@Setter
@Slf4j
public class AnnualClassResultSheetBuilder extends BaseReportBuilder {

    private final Period period;

    private final Classroom classroom;

    private List<StudentResultByLanguage> resultsByLanguage;

    private Double minAvg = 20., maxAvg = 0., classAvg = 0., successRate;

    private Integer passed = 0;

    protected AnnualClassResultSheetBuilder(Period period, Classroom classroom, String lang) {
        super(PageType.A4, PageOrientation.PORTRAIT);
        this.period = period;
        this.classroom = classroom;
        locale = Locale.forLanguageTag(lang);
        var resultByLanguageRepo = (StudentResultByLanguageRepository) Application.repositories.getRepositoryFor(StudentResultByLanguage.class).get();
        resultsByLanguage = resultByLanguageRepo.findByStudent_ClassroomAndLanguage(classroom, Language.valueOf(lang))
                .stream().filter(rs -> (rs.getTerm2Average() != null && rs.getTerm2Average() > 0)).toList();
    }

    @Override
    protected void configureMainDatasource() {
        var ds = new DRDataSource("studentName", "gender", "m1", "m2", "m3", "m4", "m5", "m6", "year", "rank", "app");
        for (StudentResultByLanguage rs : resultsByLanguage) {
            ds.add(rs.getStudent().getFullName(),
                    rs.getStudent().getGender().name(),
                    rs.getEval1Average(),
                    rs.getEval2Average(),
                    rs.getEval3Average(),
                    rs.getEval4Average(),
                    rs.getEval5Average(),
                    rs.getEval6Average(),
                    rs.getYearAverage(),
                    rankFormat(rs.getYearRank(), locale),
                    rs.getYearAppreciation()
            );
            if (rs.getYearAverage() < minAvg) minAvg = rs.getYearAverage();
            if (rs.getYearAverage() > maxAvg) maxAvg = rs.getYearAverage();
            classAvg += Utils.ifNull(rs.getYearAverage(), 0.);
            passed += (Utils.ifNull(rs.getYearAverage(), 0.) >= 10 ? 1 : 0);
        }
        classAvg = classAvg / resultsByLanguage.size();
        successRate = passed * 100. / resultsByLanguage.size();
        dataSource = ds;
    }

    @Override
    public BaseReportBuilder buildReport() {
        super.buildReport();
        titleField.setText(exp.message("RS.annual_title", new Object[]{Constants.CURRENT_SCHOOL_YEAR}));
        columnsWidths = new int[]{1, 5, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        columnsTitleKeys = new String[]{null, "shared.student_name", "shared.gender", "RC.month1", "RC.month2", "RC.month3", "RC.month4", "RC.month5", "RC.month6", "RC.year", "RC.rank", "RC.appreciation_short"};
        TextColumnBuilder studentName, gender, m1, m2, m3, m4, m5, m6, avg, rank, app;
        columnsBuilders = List.of(
                col.column(exp.reportRowNumber()).setPattern("00").setTitle("N<sup>o</sup>"),
                studentName = col.column("studentName", String.class),
                col.column("gender", String.class),
                m1 = col.column("m1", Double.class).setPattern(averagePattern),
                m2 = col.column("m2", Double.class).setPattern(averagePattern),
                m3 = col.column("m3", Double.class).setPattern(averagePattern),
                m4 = col.column("m4", Double.class).setPattern(averagePattern),
                m5 = col.column("m5", Double.class).setPattern(averagePattern),
                m6 = col.column("m6", Double.class).setPattern(averagePattern),
                avg = col.column("year", Double.class).setPattern(averagePattern),
                rank = col.column("rank", String.class),
                app = col.column("app", String.class)
        );
        configureColumnsBuilders(columnsWidths, columnsTitleKeys, columnsBuilders);
        columnsBuilders.get(1).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
        List.of(avg, rank, app).forEach(col->col.setStyle(Styles.boldCentered1ptBorderStyle));

        pageHeader(createHeader().setPrintWhenExpression(exp.printInFirstPage()))
                .sortBy(asc(studentName))
                .summary(createDoneAtOn().add(createTeacherAndHeadMasterSignature()))
                .columns(columnsBuilders.toArray(new TextColumnBuilder[0]))
                .setColumnTitleStyle(Styles.reportCardColumnTitleStyle);

        return this;
    }

    @Override
    public HorizontalListBuilder createHeader() {
        var header = super.createHeader();
        List<TextFieldBuilder> label1 = List.of(
                cmp.text(exp.message("shared.class")),
                cmp.text(exp.message("shared.enrolled")),
                cmp.text(exp.message("RC.max_avg")),
                cmp.text(exp.message("RC.min_avg"))
        );
        List<TextFieldBuilder> value1 = List.of(
                cmp.text(String.format(": %s", classroom.getName())),
                cmp.text(String.format(": %d", resultsByLanguage.size())),
                cmp.text(String.format(": %5.2f", maxAvg)),
                cmp.text(String.format(": %5.2f", minAvg))
        );
        List<TextFieldBuilder> label2 = List.of(
                cmp.text(exp.message("RC.class_avg_short")),
                cmp.text(exp.message("RC.above_avg")),
                cmp.text(exp.message("RC.below_avg")),
                cmp.text(exp.message("RC.success_rate"))
        );
        List<TextFieldBuilder> value2 = List.of(
                cmp.text(String.format(": %5.2f", classAvg)),
                cmp.text(String.format(": %d", passed)),
                cmp.text(String.format(": %d", (resultsByLanguage.size() - passed))),
                cmp.text(String.format(": %5.2f %%", successRate))
        );
        label1.forEach(label -> label.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT));
        label2.forEach(label -> label.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT));
        value1.forEach(val -> val.setStyle(Styles.boldStyle));
        value2.forEach(val -> val.setStyle(Styles.boldStyle));
        return header.newRow().add(
                cmp.verticalList(label1.toArray(new TextFieldBuilder[0])).setWidth(1),
                cmp.verticalList(value1.toArray(new TextFieldBuilder[0])).setWidth(2),
                cmp.verticalList(label2.toArray(new TextFieldBuilder[0])).setWidth(1),
                cmp.verticalList(value2.toArray(new TextFieldBuilder[0])).setWidth(1)
        );
    }
}
