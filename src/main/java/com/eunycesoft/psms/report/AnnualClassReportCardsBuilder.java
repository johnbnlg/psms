package com.eunycesoft.psms.report;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.Constants;
import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.entity.StudentResult;
import com.eunycesoft.psms.data.entity.StudentResultByGroup;
import com.eunycesoft.psms.data.entity.StudentResultByLanguage;
import com.eunycesoft.psms.data.entity.StudentResultBySubject;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.enums.Language;
import com.eunycesoft.psms.data.enums.Period;
import com.eunycesoft.psms.data.enums.Section;
import com.eunycesoft.psms.data.repository.StudentResultByGroupRepository;
import com.eunycesoft.psms.data.repository.StudentResultByLanguageRepository;
import com.eunycesoft.psms.data.repository.StudentResultBySubjectRepository;
import com.eunycesoft.psms.data.repository.StudentResultRepository;
import lombok.extern.slf4j.Slf4j;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.FieldBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.constant.*;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.jasperreports.engine.JRDataSource;

import java.text.MessageFormat;
import java.util.*;

import static com.eunycesoft.psms.Constants.averagePattern;
import static net.sf.dynamicreports.report.builder.DynamicReports.*;


@Slf4j
public class AnnualClassReportCardsBuilder extends BaseReportBuilder {


    private final StudentResultBySubjectRepository resultBySubjectRepository;

    private final StudentResultByGroupRepository resultByGroupRepository;

    private final StudentResultByLanguageRepository resultByLanguageRepository;

    private final StudentResultRepository resultRepository;

    List<StudentResultBySubject> resultBySubjectList = new ArrayList<>();
    private List<StudentResultByGroup> resultByGroupList = new ArrayList<>();
    private List<StudentResultByLanguage> resultByLanguageList = new ArrayList<>();
    private List<StudentResult> resultList = new ArrayList<>();

    private final Classroom classroom;

    private final Language lang;

    private final Period period;

    private double eval1Max = 0, eval1Min = 20., eval1Avg = 0., eval1Rate = 0.;
    private double eval2Max = 0, eval2Min = 20., eval2Avg = 0., eval2Rate = 0.;
    private double eval3Max = 0, eval3Min = 20., eval3Avg = 0., eval3Rate = 0.;
    private double eval4Max = 0, eval4Min = 20., eval4Avg = 0., eval4Rate = 0.;
    private double eval5Max = 0, eval5Min = 20., eval5Avg = 0., eval5Rate = 0.;
    private double eval6Max = 0, eval6Min = 20., eval6Avg = 0., eval6Rate = 0.;
    private double annualMax = 0., annualMin = 20., annualAvg = 0., annualRate = 0.;

    private int listElement = 0;

    protected AnnualClassReportCardsBuilder(Period period, Classroom classroom, String lang) {
        super(PageType.A4, PageOrientation.PORTRAIT);
        this.classroom = classroom;
        this.lang = Language.valueOf(lang);
        this.period = period;
        locale = Locale.forLanguageTag(lang);
        resultBySubjectRepository = (StudentResultBySubjectRepository) Application.repositories.getRepositoryFor(StudentResultBySubject.class).get();
        resultByGroupRepository = (StudentResultByGroupRepository) Application.repositories.getRepositoryFor(StudentResultByGroup.class).get();
        resultByLanguageRepository = (StudentResultByLanguageRepository) Application.repositories.getRepositoryFor(StudentResultByLanguage.class).get();
        resultRepository = (StudentResultRepository) Application.repositories.getRepositoryFor(StudentResult.class).get();
    }

    public void computeClassroomSummary() {
        int enrolled1 = 0, enrolled2 = 0, enrolled3 = 0, enrolled4 = 0, enrolled5 = 0, enrolled6 = 0, enrolled = 0;
        for (StudentResultByLanguage rs : resultByLanguageList) {
            if (rs.getYearAverage() == null || rs.getYearAverage() == 0) continue;

            if (rs.getEval1Average() != null) {
                eval1Max = Double.max(eval1Max, Utils.ifNull(rs.getEval1Average(), 0.));
                eval1Min = Double.min(eval1Min, Utils.ifNull(rs.getEval1Average(), 20.));
                eval1Avg = eval1Avg + Utils.ifNull(rs.getEval1Average(), 0.);
                eval1Rate = eval1Rate + (rs.getEval1Average() != null && rs.getEval1Average() >= 10 ? 1 : 0);
                enrolled1++;
            }

            if (rs.getEval2Average() != null) {
                eval2Max = Double.max(eval2Max, Utils.ifNull(rs.getEval2Average(), 0.));
                eval2Min = Double.min(eval2Min, Utils.ifNull(rs.getEval2Average(), 20.));
                eval2Avg = eval2Avg + Utils.ifNull(rs.getEval2Average(), 0.);
                eval2Rate = eval2Rate + (rs.getEval2Average() != null && rs.getEval2Average() >= 10 ? 1 : 0);
                enrolled2++;
            }

            if (rs.getEval3Average() != null) {
                eval3Max = Double.max(eval3Max, Utils.ifNull(rs.getEval3Average(), 0.));
                eval3Min = Double.min(eval3Min, Utils.ifNull(rs.getEval3Average(), 20.));
                eval3Avg = eval3Avg + Utils.ifNull(rs.getEval3Average(), 0.);
                eval3Rate = eval3Rate + (rs.getEval3Average() != null && rs.getEval3Average() >= 10 ? 1 : 0);
                enrolled3++;
            }
            if (rs.getEval4Average() != null) {
                eval4Max = Double.max(eval4Max, Utils.ifNull(rs.getEval4Average(), 0.));
                eval4Min = Double.min(eval4Min, Utils.ifNull(rs.getEval4Average(), 20.));
                eval4Avg = eval4Avg + Utils.ifNull(rs.getEval4Average(), 0.);
                eval4Rate = eval4Rate + (rs.getEval4Average() != null && rs.getEval4Average() >= 10 ? 1 : 0);
                enrolled4++;
            }

            if (rs.getEval5Average() != null) {
                eval5Max = Double.max(eval5Max, Utils.ifNull(rs.getEval5Average(), 0.));
                eval5Min = Double.min(eval5Min, Utils.ifNull(rs.getEval5Average(), 20.));
                eval5Avg = eval5Avg + Utils.ifNull(rs.getEval5Average(), 0.);
                eval5Rate = eval5Rate + (rs.getEval5Average() != null && rs.getEval5Average() >= 10 ? 1 : 0);
                enrolled5++;
            }

            if (rs.getEval6Average() != null) {
                eval6Max = Double.max(eval6Max, Utils.ifNull(rs.getEval6Average(), 0.));
                eval6Min = Double.min(eval6Min, Utils.ifNull(rs.getEval6Average(), 20.));
                eval6Avg = eval6Avg + Utils.ifNull(rs.getEval6Average(), 0.);
                eval6Rate = eval6Rate + (rs.getEval6Average() != null && rs.getEval6Average() >= 10 ? 1 : 0);
                enrolled6++;
            }

            annualMax = Double.max(annualMax, Utils.ifNull(rs.getTerm2Average(), 0.));
            annualMin = Double.min(annualMin, Utils.ifNull(rs.getTerm2Average(), 20.));
            annualAvg = annualAvg + Utils.ifNull(rs.getYearAverage(), 0.);
            annualRate = annualRate + (rs.getYearAverage() != null && rs.getYearAverage() >= 10 ? 1 : 0);
            enrolled++;
        }

        eval1Avg = eval1Avg / enrolled1;
        eval2Avg = eval2Avg / enrolled2;
        eval3Avg = eval3Avg / enrolled3;
        eval4Avg = eval4Avg / enrolled4;
        eval5Avg = eval5Avg / enrolled5;
        eval6Avg = eval6Avg / enrolled6;
        annualAvg = annualAvg / enrolled;

        eval1Rate = eval1Rate * 100. / enrolled1;
        eval2Rate = eval2Rate * 100. / enrolled2;
        eval3Rate = eval3Rate * 100. / enrolled3;
        eval4Rate = eval4Rate * 100. / enrolled4;
        eval5Rate = eval5Rate * 100. / enrolled5;
        eval6Rate = eval6Rate * 100. / enrolled6;
        annualRate = annualRate * 100. / enrolled;
    }

    @Override
    protected void configureMainDatasource() {
        resultByGroupList = resultByGroupRepository.findByStudent_Classroom(classroom);
        resultByLanguageList = resultByLanguageRepository.findByStudent_ClassroomAndLanguage(classroom, lang);
        computeClassroomSummary();
        if (classroom.getSection() == Section.BIL)
            resultList = resultRepository.findByStudent_Classroom(classroom);
        var ds = new DRDataSource("studentId", "studentName", "subjectGroupId", "subjectGroupName", "subjectId", "subjectName",
                "markOver", "m1", "m2", "m3", "m4", "m5", "m6", "year", "rank", "app");
        resultBySubjectList = resultBySubjectRepository.findByStudent_ClassroomAndClassroomSubject_Language(classroom, lang);
        resultBySubjectList.sort(Comparator.comparing(rs -> rs.getStudent().getFullName()));
        resultBySubjectList.forEach(rs -> {
            ds.add(rs.getStudent().getId(),
                    rs.getStudent().getFullName(),
                    rs.getClassroomSubject().getSubject().getSubjectGroup().getId(),
                    rs.getClassroomSubject().getSubject().getSubjectGroup().toString(locale),
                    rs.getClassroomSubject().getSubject().getId(),
                    rs.getClassroomSubject().getSubject().toString(locale),
                    rs.getClassroomSubject().getMarkedOver(),
                    rs.getEval1Average(),
                    rs.getEval2Average(),
                    rs.getEval3Average(),
                    rs.getEval4Average(),
                    rs.getEval5Average(),
                    rs.getEval6Average(),
                    rs.getYearAverage(),
                    Utils.rankFormat(rs.getYearRank(), locale),
                    rs.getYearAppreciation());
        });
        dataSource = ds;
    }

    @Override
    public BaseReportBuilder buildReport() {
        super.buildReport();

        titleField.setText(exp.message("RC.annual_title", new Object[]{Constants.CURRENT_SCHOOL_YEAR}))
                .setStyle(Styles.A4PortraitReportTitleStyle);

        columnsWidths = new int[]{5, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        columnsTitleKeys = new String[]{"RC.subjectName", "RC.marked_over", "RC.month1", "RC.month2", "RC.month3", "RC.month4", "RC.month5", "RC.month6", "RC.year", "RC.rank", "RC.appreciation_short"};
        TextColumnBuilder<?> subjectName, markover, m1, m2, m3, m4, m5, m6, year, rank, app;
        columnsBuilders = List.of(
                subjectName = col.column("subjectName", String.class),
                markover = col.column("markOver", Double.class),
                m1 = col.column("m1", Double.class).setPattern(averagePattern),
                m2 = col.column("m2", Double.class).setPattern(averagePattern),
                m3 = col.column("m3", Double.class).setPattern(averagePattern),
                m4 = col.column("m4", Double.class).setPattern(averagePattern),
                m5 = col.column("m5", Double.class).setPattern(averagePattern),
                m6 = col.column("m6", Double.class).setPattern(averagePattern),
                year = col.column("year", Double.class).setPattern(averagePattern),
                rank = col.column("rank", String.class),
                app = col.column("app", String.class)
        );
        configureColumnsBuilders(columnsWidths, columnsTitleKeys, columnsBuilders);
        columnsBuilders.get(0).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
        FieldBuilder<?> rankField = field("rank", String.class);
        FieldBuilder<?> sgField = field("subjectGroupName", String.class);

        ComponentBuilder<?, ?> reportCardFooter = cmp.horizontalList(
                cmp.text(exp.message("RC.annual_summary")).setStyle(Styles.reportCardBlueBg1ptBorderStyle)
        ).newRow().add(
                cmp.text(exp.message("RC.student_perfomance")).setStyle(Styles.reportCardGreenBg1ptBorderStyle),
                cmp.text(exp.message("RC.class_performance")).setStyle(Styles.reportCardGreenBg1ptBorderStyle)
        ).newRow().add(
                cmp.subreport(new ReportStudentResult()).setDataSource(new StudentResultDatasoure()),
                cmp.subreport(new ReportStudentResult()).setDataSource(new ClassroomResultDatasoure())
        );

        var groupByStudent = grp.group("groupByStudent", "studentId", Integer.class)
                .setHeaderLayout(GroupHeaderLayout.EMPTY)
                .startInNewPage()
                .setPadding(0)
                .footer(reportCardFooter);

        if (classroom.getSection() == Section.BIL)
            groupByStudent.addFooterComponent(cmp.subreport(new BilingualResult()));
        groupByStudent.addFooterComponent(createTeacherParentAndHeadmasterSignature());

        var groupBysubjectGroup = grp.group("groupBysubjectGroup", sgField)
                .setHeaderLayout(GroupHeaderLayout.EMPTY)
                .setPadding(0)
                .header(cmp.text(sgField).setStyle(Styles.reportCardSubjectGroupHeaderStyle))
                .footer(cmp.subreport(new SubjectGroupFooter()));
        var groupMarkedOver = variable("groupMarkedOver", markover, Calculation.SUM)
                .setResetGroup(groupBysubjectGroup)
                .setResetType(Evaluation.GROUP);
        var group = variable("group", "subjectGroupId", Integer.class, Calculation.NOTHING)
                .setResetGroup(groupBysubjectGroup)
                .setResetType(Evaluation.GROUP);
        var student = variable("student", "studentId", Integer.class, Calculation.NOTHING)
                .setResetGroup(groupByStudent)
                .setInitialValueExpression(exp.jasperSyntax("$F{studentId}"))
                .setResetType(Evaluation.GROUP);

        pageHeader(createHeader(), cmp.subreport(new ReportCardHeader()))
                .fields(rankField, sgField)
//                .setWhenResourceMissingType(WhenResourceMissingType.EMPTY)
                .variables(groupMarkedOver, group, student)
                .groupBy(groupByStudent, groupBysubjectGroup)
                .sortBy(asc("studentName", String.class), asc("subjectGroupId", Integer.class), asc("subjectId", Integer.class))
                .columns(columnsBuilders.toArray(new TextColumnBuilder[0]))
                .setColumnTitleStyle(Styles.reportCardColumnTitleStyle);
        return this;
    }

    private class ReportCardHeader extends AbstractSimpleExpression<JasperReportBuilder> {
        @Override
        public JasperReportBuilder evaluate(ReportParameters params) {
            var sdtid = (int) params.getValue("student");
            var currentStudent = classroom.getStudents().stream()
                    .filter(sdt -> sdt.getId() == sdtid).findFirst().get();
            List<TextFieldBuilder> label1 = List.of(
                    cmp.text(params.getMessage("shared.student_name")),
                    cmp.text(params.getMessage("shared.gender")),
                    cmp.text(params.getMessage("shared.date_place_birth")),
                    cmp.text(params.getMessage("shared.teacher"))
            );
            List<TextFieldBuilder> value1 = List.of(
                    cmp.text(currentStudent.getFullName()),
                    cmp.text(currentStudent.getGender().toString(locale)),
                    cmp.text(getBirthString(currentStudent)),
                    cmp.text("")
            );
            List<TextFieldBuilder> label2 = List.of(
                    cmp.text(params.getMessage("shared.reg_number")),
                    cmp.text(params.getMessage("shared.class")),
                    cmp.text(params.getMessage("shared.enrolled")),
                    cmp.text(params.getMessage("shared.repeater"))
            );
            List<TextFieldBuilder> value2 = List.of(
                    cmp.text(currentStudent.getRegistrationNumber()),
                    cmp.text(classroom.getName()),
                    cmp.text(resultByLanguageList.stream().filter(rs -> rs.getTerm2Average() != null && rs.getTerm2Average() > 0).count()),
                    cmp.text(booleanToYesNo(currentStudent.isRepeater()))
            );
            label1.forEach(label -> label.setTextAdjust(TextAdjust.SCALE_FONT).setStyle(Styles.border1ptStyle));
            label2.forEach(label -> label.setTextAdjust(TextAdjust.SCALE_FONT).setStyle(Styles.border1ptStyle));
            value1.forEach(val -> val.setTextAdjust(TextAdjust.SCALE_FONT).setStyle(Styles.bold1ptBorderStyle));
            value2.forEach(val -> val.setTextAdjust(TextAdjust.SCALE_FONT).setStyle(Styles.bold1ptBorderStyle));
            var subReport = report()
                    .setPageMargin(margin().setBottom(5))
                    .noData(
                            cmp.horizontalList(
                                    cmp.verticalList(label1.toArray(new TextFieldBuilder[0])).setWidth(1),
                                    cmp.verticalList(value1.toArray(new TextFieldBuilder[0])).setWidth(2),
                                    cmp.verticalList(label2.toArray(new TextFieldBuilder[0])).setWidth(1),
                                    cmp.verticalList(value2.toArray(new TextFieldBuilder[0])).setWidth(1),
                                    cmp.image("").setDimension(1, 1).setStyle(Styles.border1ptStyle)
                            )
                    );
            return subReport;
        }
    }

    private class SubjectGroupFooter extends AbstractSimpleExpression<JasperReportBuilder> {

        @Override
        public JasperReportBuilder evaluate(ReportParameters params) {
            var group = (int) params.getValue("group");
            var sdtid = (int) params.getValue("student");
            var groupResult = resultByGroupList.stream()
                    .filter(arg -> (arg.getSubjectGroup().getId() == group
                                    && arg.getLanguage() == lang
                                    && arg.getStudent().getId() == sdtid))
                    .findFirst().get();
            var groupSummary = cmp.text(params.getMessage("RC.group_summary"));
            List<TextFieldBuilder> textFieldBuilders = List.of(
                    groupSummary,
                    cmp.text("N/A"),
                    cmp.text(groupResult.getEval1Average()),
                    cmp.text(groupResult.getEval2Average()),
                    cmp.text(groupResult.getEval3Average()),
                    cmp.text(groupResult.getEval4Average()),
                    cmp.text(groupResult.getEval5Average()),
                    cmp.text(groupResult.getEval6Average()),
                    cmp.text(groupResult.getYearAverage()),
                    cmp.text(Utils.rankFormat(groupResult.getYearRank(), locale)),
                    cmp.text(groupResult.getYearAppreciation())
            );
            textFieldBuilders.forEach(tfb -> tfb.setTextAdjust(Styles.defaultTextAdjust).setPattern(averagePattern).setWidth(1).setStyle(Styles.reportCardSubjectGroupFooterStyle));
            groupSummary.setWidth(5).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
            textFieldBuilders.get(1).setPattern("00.00");
            return report().pageHeader(cmp.horizontalList(textFieldBuilders.toArray(new TextFieldBuilder[0])));
        }
    }

    private class ReportStudentResult extends AbstractSimpleExpression<JasperReportBuilder> {

        @Override
        public JasperReportBuilder evaluate(ReportParameters params) {
            TextColumnBuilder<?> label, m1, m2, m3, m4, m5, m6, year;
            var columns = Arrays.asList(
                    label = col.column("", "label", String.class),
                    m1 = col.column(params.getMessage("RC.month1"), "m1", String.class),
                    m2 = col.column(params.getMessage("RC.month2"), "m2", String.class),
                    m3 = col.column(params.getMessage("RC.month3"), "m3", String.class),
                    m4 = col.column(params.getMessage("RC.month4"), "m4", String.class),
                    m5 = col.column(params.getMessage("RC.month5"), "m5", String.class),
                    m6 = col.column(params.getMessage("RC.month6"), "m6", String.class),
                    year = col.column(params.getMessage("RC.year"), "year", String.class)
            );
            var subreport = report();
            subreport.setWhenResourceMissingType(WhenResourceMissingType.KEY)
                    .columns(columns.toArray(new TextColumnBuilder[0]))
                    .setColumnStyle(Styles.centeredItalic1ptBorderStyle)
                    .setColumnTitleStyle(Styles.reportCardColumnTitleStyle);
            label.setStyle(Styles.reportCardColumnTitleStyle);
            year.setStyle(Styles.boldCentered1ptBorderStyle);
            columns.forEach(col -> col.setTextAdjust(Styles.defaultTextAdjust).setTitleTextAdjust(Styles.defaultTextAdjust));
            return subreport;
        }
    }

    private class StudentResultDatasoure extends AbstractSimpleExpression<JRDataSource> {

        @Override
        public JRDataSource evaluate(ReportParameters params) {
            var sdtid = (int) params.getValue("student");
            var ds = new DRDataSource("label", "m1", "m2", "m3", "m4", "m5", "m6", "year");

            var r = resultByLanguageList.stream()
                    .filter(rbl -> rbl.getStudent().getId() == sdtid && rbl.getLanguage().equals(lang))
                    .findFirst().get();
            ds.add(
                    params.getMessage("RC.total"),
                    Utils.formatDoubleNumber(r.getEval1total()),
                    Utils.formatDoubleNumber(r.getEval2total()),
                    Utils.formatDoubleNumber(r.getEval3total()),
                    Utils.formatDoubleNumber(r.getEval4total()),
                    Utils.formatDoubleNumber(r.getEval5total()),
                    Utils.formatDoubleNumber(r.getEval6total()),
                    "N/A"
            );
            ds.add(
                    params.getMessage("RC.average_short"),
                    Utils.formatDoubleNumber(r.getEval1Average()),
                    Utils.formatDoubleNumber(r.getEval2Average()),
                    Utils.formatDoubleNumber(r.getEval3Average()),
                    Utils.formatDoubleNumber(r.getEval4Average()),
                    Utils.formatDoubleNumber(r.getEval5Average()),
                    Utils.formatDoubleNumber(r.getEval6Average()),
                    Utils.formatDoubleNumber(r.getYearAverage())
            );
            ds.add(
                    params.getMessage("RC.rank"),
                    Utils.rankFormat(r.getEval1Rank(), locale),
                    Utils.rankFormat(r.getEval2Rank(), locale),
                    Utils.rankFormat(r.getEval3Rank(), locale),
                    Utils.rankFormat(r.getEval4Rank(), locale),
                    Utils.rankFormat(r.getEval5Rank(), locale),
                    Utils.rankFormat(r.getEval6Rank(), locale),
                    Utils.rankFormat(r.getYearRank(), locale)
            );
            ds.add(
                    params.getMessage("RC.appreciation_short"),
                    r.getEval1Appreciation(),
                    r.getEval2Appreciation(),
                    r.getEval3Appreciation(),
                    r.getEval4Appreciation(),
                    r.getEval5Appreciation(),
                    r.getEval6Appreciation(),
                    r.getYearAppreciation()
            );
            return ds;
        }
    }

    private class ClassroomResultDatasoure extends AbstractSimpleExpression<JRDataSource> {

        @Override
        public JRDataSource evaluate(ReportParameters params) {
            var ds = new DRDataSource("label", "m1", "m2", "m3", "m4", "m5", "m6", "year");
            ds.add(
                    params.getMessage("RC.class_avg_short"),
                    Utils.formatDoubleNumber(eval1Avg),
                    Utils.formatDoubleNumber(eval2Avg),
                    Utils.formatDoubleNumber(eval3Avg),
                    Utils.formatDoubleNumber(eval4Avg),
                    Utils.formatDoubleNumber(eval5Avg),
                    Utils.formatDoubleNumber(eval6Avg),
                    Utils.formatDoubleNumber(annualAvg)
            );
            ds.add(
                    params.getMessage("RC.max_avg"),
                    Utils.formatDoubleNumber(eval1Max),
                    Utils.formatDoubleNumber(eval2Max),
                    Utils.formatDoubleNumber(eval3Max),
                    Utils.formatDoubleNumber(eval4Max),
                    Utils.formatDoubleNumber(eval5Max),
                    Utils.formatDoubleNumber(eval6Max),
                    Utils.formatDoubleNumber(annualMax)
            );
            ds.add(
                    params.getMessage("RC.min_avg"),
                    Utils.formatDoubleNumber(eval1Min),
                    Utils.formatDoubleNumber(eval2Min),
                    Utils.formatDoubleNumber(eval3Min),
                    Utils.formatDoubleNumber(eval4Min),
                    Utils.formatDoubleNumber(eval5Min),
                    Utils.formatDoubleNumber(eval6Min),
                    Utils.formatDoubleNumber(annualMin)
            );
            ds.add(
                    params.getMessage("RC.success_rate_short"),
                    Utils.formatDoubleNumber(eval1Rate),
                    Utils.formatDoubleNumber(eval2Rate),
                    Utils.formatDoubleNumber(eval3Rate),
                    Utils.formatDoubleNumber(eval4Rate),
                    Utils.formatDoubleNumber(eval5Rate),
                    Utils.formatDoubleNumber(eval6Rate),
                    Utils.formatDoubleNumber(annualRate)
            );
            return ds;
        }
    }

    private class BilingualResult extends AbstractSimpleExpression<JasperReportBuilder> {

        @Override
        public JasperReportBuilder evaluate(ReportParameters params) {
            var sdtid = (int) params.getValue("student");
            var rsa = resultList.stream()
                    .filter(r -> r.getStudent().getId() == sdtid)
                    .findFirst().get();
            var rsFR = resultByLanguageRepository.findByStudent_IdAndLanguage(sdtid, Language.FR);
            var rsEN = resultByLanguageRepository.findByStudent_IdAndLanguage(sdtid, Language.EN);

            return report().noData(
                    cmp.horizontalList(
                            cmp.text("Sous-système francophone").setStyle(Styles.reportCardGreenBg1ptBorderStyle),
                            cmp.text("Anglophone subsystem").setStyle(Styles.reportCardGreenBg1ptBorderStyle),
                            cmp.text(exp.message("RC.global_result")).setStyle(Styles.reportCardGreenBg1ptBorderStyle)
                    ).newRow().add(
                            cmp.text(MessageFormat.format("MOY: {0, number,00.00} &emsp RANG: {1} &emsp APPR.: {2}",
                                    rsFR.getYearAverage(), Utils.rankFormat(rsFR.getYearRank(), Locale.FRENCH), rsFR.getYearAppreciation())
                            ).setTextAdjust(TextAdjust.SCALE_FONT).setStyle(Styles.boldCentered1ptBorderStyle),
                            cmp.text(MessageFormat.format("AVG: {0, number,00.00} &emsp RANK: {1} &emsp APPR.: {2}",
                                    rsEN.getYearAverage(), Utils.rankFormat(rsEN.getYearRank(), Locale.ENGLISH), rsEN.getYearAppreciation())
                            ).setTextAdjust(TextAdjust.SCALE_FONT).setStyle(Styles.boldCentered1ptBorderStyle),
                            cmp.text(params.getMessage("RC.global_summary",
                                    new Object[]{rsa.getYearAverage(), Utils.rankFormat(rsa.getYearRank(), locale), rsa.getYearAppreciation()})
                            ).setTextAdjust(TextAdjust.SCALE_FONT).setStyle(Styles.boldCentered1ptBorderStyle)
                    )
            );
        }
    }
}