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
public class TermClassReportCardsBuilder extends BaseReportBuilder {


    private final StudentResultBySubjectRepository resultBySubjectRepository;

    private final StudentResultByGroupRepository resultByGroupRepository;

    private final StudentResultByLanguageRepository resultByLanguageRepository;

    private final StudentResultRepository resultRepository;

    //    @Autowired
//    private AnnualClassroomResultByLanguageRepository classroomResultByLanguageRepository;

    List<StudentResultBySubject> resultBySubjectList = new ArrayList<>();
    private List<StudentResultByGroup> resultByGroupList = new ArrayList<>();
    private List<StudentResultByLanguage> resultByLanguageList = new ArrayList<>();
    private List<com.eunycesoft.psms.data.entity.StudentResult> resultList = new ArrayList<>();

    private final Classroom classroom;

    private final com.eunycesoft.psms.data.enums.Language lang;

    private final Period period;

    private double eval5Max = 0, eval5Min = 20., eval5Avg = 0., eval5Rate = 0.;
    private double eval6Max = 0, eval6Min = 20., eval6Avg = 0., eval6Rate = 0.;
    private double term3Max = 0., term3Min = 20., term3Avg = 0., term3Rate = 0.;

    private int listElement = 0;

    protected TermClassReportCardsBuilder(Period period, Classroom classroom, String lang) {
        super(PageType.A4, PageOrientation.PORTRAIT);
        this.classroom = classroom;
        this.lang = com.eunycesoft.psms.data.enums.Language.valueOf(lang);
        this.period = period;
        locale = Locale.forLanguageTag(lang);
        resultBySubjectRepository = (StudentResultBySubjectRepository) Application.repositories.getRepositoryFor(StudentResultBySubject.class).get();
        resultByGroupRepository = (StudentResultByGroupRepository) Application.repositories.getRepositoryFor(StudentResultByGroup.class).get();
        resultByLanguageRepository = (StudentResultByLanguageRepository) Application.repositories.getRepositoryFor(StudentResultByLanguage.class).get();
        resultRepository = (StudentResultRepository) Application.repositories.getRepositoryFor(StudentResult.class).get();
    }

    public void computeClassroomSummary() {
        int enrolled = 0;
        for (StudentResultByLanguage rs : resultByLanguageList) {
            if (rs.getTerm3Average() == null || rs.getTerm3Average() == 0) continue;
            if (rs.getEval5Average() != null) {
                eval5Max = Double.max(eval5Max, Utils.ifNull(rs.getEval5Average(), 0.));
                eval5Min = Double.min(eval5Min, Utils.ifNull(rs.getEval5Average(), 20.));
                eval5Avg = eval5Avg + Utils.ifNull(rs.getEval5Average(), 0.);
                eval5Rate = eval5Rate + (rs.getEval5Average() != null && rs.getEval5Average() >= 10 ? 1 : 0);
            }
            if (rs.getEval6Average() != null) {
                eval6Max = Double.max(eval6Max, Utils.ifNull(rs.getEval6Average(), 0.));
                eval6Min = Double.min(eval6Min, Utils.ifNull(rs.getEval6Average(), 20.));
                eval6Avg = eval6Avg + Utils.ifNull(rs.getEval6Average(), 0.);
                eval6Rate = eval6Rate + (rs.getEval6Average() != null && rs.getEval6Average() >= 10 ? 1 : 0);
            }

            term3Max = Double.max(term3Max, Utils.ifNull(rs.getTerm3Average(), 0.));
            term3Min = Double.min(term3Min, Utils.ifNull(rs.getTerm3Average(), 20.));
            term3Avg = term3Avg + Utils.ifNull(rs.getTerm3Average(), 0.);
            term3Rate = term3Rate + (rs.getTerm3Average() != null && rs.getTerm3Average() >= 10 ? 1 : 0);
            enrolled++;
        }
        eval5Avg = eval5Avg / enrolled;
        eval6Avg = eval6Avg / enrolled;
        term3Avg = term3Avg / enrolled;

        eval5Rate = eval5Rate * 100. / enrolled;
        eval6Rate = eval6Rate * 100. / enrolled;
        term3Rate = term3Rate * 100. / enrolled;
    }

    @Override
    protected void configureMainDatasource() {
        resultByGroupList = resultByGroupRepository.findByStudent_Classroom(classroom);
        resultByLanguageList = resultByLanguageRepository.findByStudent_ClassroomAndLanguage(classroom, lang);
        computeClassroomSummary();
        if (classroom.getSection() == Section.BIL)
            resultList = resultRepository.findByStudent_Classroom(classroom);
        var ds = new DRDataSource("studentId", "studentName", "subjectGroupId", "subjectGroupName", "subjectId", "subjectName",
                "markOver", "m1", "m2", "term", "rank", "app");
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
                    rs.getEval5Average(),
                    rs.getEval6Average(),
                    rs.getTerm3Average(),
                    Utils.rankFormat(rs.getTerm3Rank(), locale),
                    rs.getTerm3Appreciation());
        });
        dataSource = ds;
    }

    @Override
    public BaseReportBuilder buildReport() {
        super.buildReport();

        titleField.setText(exp.message("RC.period_title", new Object[]{isReportInFrench() ? "TROISIÈME TRIMESTRE" : "THIRD TERM", Constants.CURRENT_SCHOOL_YEAR}))
                .setStyle(Styles.A4PortraitReportTitleStyle);

        columnsWidths = new int[]{5, 1, 1, 1, 1, 1, 1};
        columnsTitleKeys = new String[]{"RC.subjectName", "RC.marked_over", "RC.month5", "RC.month6", "RC.term3", "RC.rank", "RC.appreciation_short"};
        TextColumnBuilder<?> subjectName, markover, m1, m2, term, rank, app;
        columnsBuilders = List.of(
                subjectName = col.column("subjectName", String.class),
                markover = col.column("markOver", Double.class),
                m1 = col.column("m1", Double.class).setPattern(averagePattern),
                m2 = col.column("m2", Double.class).setPattern(averagePattern),
                term = col.column("term", Double.class).setPattern(averagePattern),
                rank = col.column("rank", String.class),
                app = col.column("app", String.class)
        );
        configureColumnsBuilders(columnsWidths, columnsTitleKeys, columnsBuilders);
        columnsBuilders.get(0).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
        FieldBuilder<?> rankField = field("rank", String.class);
        FieldBuilder<?> sgField = field("subjectGroupName", String.class);

        ComponentBuilder<?, ?> reportCardFooter = cmp.horizontalList(
                cmp.text(exp.message("RC.term_summary")).setStyle(Styles.reportCardBlueBg1ptBorderStyle)
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
                    cmp.text(resultByLanguageList.stream().filter(rs -> rs.getTerm3Average() != null && rs.getTerm3Average() > 0).count()),
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
                    cmp.text(Utils.ifNull(groupResult.getEval5over(), groupResult.getEval6over())),
                    cmp.text(groupResult.getEval5Average()),
                    cmp.text(groupResult.getEval6Average()),
                    cmp.text(groupResult.getTerm3Average()),
                    cmp.text(Utils.rankFormat(groupResult.getTerm3Rank(), locale)),
                    cmp.text(groupResult.getTerm3Appreciation())
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
            TextColumnBuilder<?> label, m1, m2, term;
            var columns = Arrays.asList(
                    label = col.column("", "label", String.class),
                    m1 = col.column(params.getMessage("RC.month5"), "m1", String.class),
                    m2 = col.column(params.getMessage("RC.month6"), "m2", String.class),
                    term = col.column(params.getMessage("RC.term3"), "term", String.class)
            );
            var subreport = report();
            subreport.setWhenResourceMissingType(WhenResourceMissingType.KEY)
                    .columns(columns.toArray(new TextColumnBuilder[0]))
                    .setColumnStyle(Styles.centeredItalic1ptBorderStyle)
                    .setColumnTitleStyle(Styles.reportCardColumnTitleStyle);
            label.setStyle(Styles.reportCardColumnTitleStyle);
            term.setStyle(Styles.boldCentered1ptBorderStyle);
            columns.forEach(col -> col.setTextAdjust(Styles.defaultTextAdjust).setTitleTextAdjust(Styles.defaultTextAdjust));
            return subreport;
        }
    }

    private class StudentResultDatasoure extends AbstractSimpleExpression<JRDataSource> {

        @Override
        public JRDataSource evaluate(ReportParameters params) {
            var sdtid = (int) params.getValue("student");
            var ds = new DRDataSource("label", "m1", "m2", "term");

            var r = resultByLanguageList.stream()
                    .filter(rbl -> rbl.getStudent().getId() == sdtid && rbl.getLanguage().equals(lang))
                    .findFirst().get();
            ds.add(
                    params.getMessage("RC.total"),
                    Utils.formatDoubleNumber(r.getEval5total()),
                    Utils.formatDoubleNumber(r.getEval6total()),
                    "N/A"
            );
            ds.add(
                    params.getMessage("RC.average_short"),
                    Utils.formatDoubleNumber(r.getEval5Average()),
                    Utils.formatDoubleNumber(r.getEval6Average()),
                    Utils.formatDoubleNumber(r.getTerm3Average())
            );
            ds.add(
                    params.getMessage("RC.rank"),
                    Utils.rankFormat(r.getEval5Rank(), locale),
                    Utils.rankFormat(r.getEval6Rank(), locale),
                    Utils.rankFormat(r.getTerm3Rank(), locale)
            );
            ds.add(
                    params.getMessage("RC.appreciation_short"),
                    r.getEval5Appreciation(),
                    r.getEval6Appreciation(),
                    r.getTerm3Appreciation()
            );
            return ds;
        }
    }

    private class ClassroomResultDatasoure extends AbstractSimpleExpression<JRDataSource> {

        @Override
        public JRDataSource evaluate(ReportParameters params) {
            var ds = new DRDataSource("label", "m1", "m2", "term");
            ds.add(
                    params.getMessage("RC.class_avg_short"),
                    Utils.formatDoubleNumber(eval5Avg),
                    Utils.formatDoubleNumber(eval6Avg),
                    Utils.formatDoubleNumber(term3Avg)
            );
            ds.add(
                    params.getMessage("RC.max_avg"),
                    Utils.formatDoubleNumber(eval5Max),
                    Utils.formatDoubleNumber(eval6Max),
                    Utils.formatDoubleNumber(term3Max)
            );
            ds.add(
                    params.getMessage("RC.min_avg"),
                    Utils.formatDoubleNumber(eval5Min),
                    Utils.formatDoubleNumber(eval6Min),
                    Utils.formatDoubleNumber(term3Min)
            );
            ds.add(
                    params.getMessage("RC.success_rate_short"),
                    Utils.formatDoubleNumber(term3Rate),
                    Utils.formatDoubleNumber(term3Rate),
                    Utils.formatDoubleNumber(term3Rate)
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
            var rsFR = resultByLanguageRepository.findByStudent_IdAndLanguage(sdtid, com.eunycesoft.psms.data.enums.Language.FR);
            var rsEN = resultByLanguageRepository.findByStudent_IdAndLanguage(sdtid, com.eunycesoft.psms.data.enums.Language.EN);

            return report().noData(
                    cmp.horizontalList(
                            cmp.text("Sous-système francophone").setStyle(Styles.reportCardGreenBg1ptBorderStyle),
                            cmp.text("Anglophone subsystem").setStyle(Styles.reportCardGreenBg1ptBorderStyle),
                            cmp.text(exp.message("RC.global_result")).setStyle(Styles.reportCardGreenBg1ptBorderStyle)
                    ).newRow().add(
                            cmp.text(MessageFormat.format("MOY: {0, number,00.00} &emsp RANG: {1} &emsp APPR.: {2}",
                                    rsFR.getTerm3Average(), Utils.rankFormat(rsFR.getTerm3Rank(), Locale.FRENCH), rsFR.getTerm3Appreciation())
                            ).setTextAdjust(TextAdjust.SCALE_FONT).setStyle(Styles.boldCentered1ptBorderStyle),
                            cmp.text(MessageFormat.format("AVG: {0, number,00.00} &emsp RANK: {1} &emsp APPR.: {2}",
                                    rsEN.getTerm3Average(), Utils.rankFormat(rsEN.getTerm3Rank(), Locale.ENGLISH), rsEN.getTerm3Appreciation())
                            ).setTextAdjust(TextAdjust.SCALE_FONT).setStyle(Styles.boldCentered1ptBorderStyle),
                            cmp.text(params.getMessage("RC.global_summary",
                                    new Object[]{rsa.getTerm3Average(), Utils.rankFormat(rsa.getTerm3Rank(), locale), rsa.getTerm3Appreciation()})
                            ).setTextAdjust(TextAdjust.SCALE_FONT).setStyle(Styles.boldCentered1ptBorderStyle)
                    )
            );
        }
    }
}