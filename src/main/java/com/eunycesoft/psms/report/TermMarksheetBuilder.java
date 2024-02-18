package com.eunycesoft.psms.report;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.data.entity.StudentResultByLanguage;
import com.eunycesoft.psms.data.entity.StudentResultBySubject;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.enums.Language;
import com.eunycesoft.psms.data.enums.Period;
import com.eunycesoft.psms.data.repository.StudentResultByLanguageRepository;
import com.eunycesoft.psms.data.repository.StudentResultBySubjectRepository;
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.constant.*;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.definition.ReportParameters;

import java.util.List;
import java.util.Locale;

import static com.eunycesoft.psms.Constants.CURRENT_SCHOOL_YEAR;
import static com.eunycesoft.psms.Utils.formatDoubleNumber;
import static com.eunycesoft.psms.Utils.rankFormat;
import static net.sf.dynamicreports.report.builder.DynamicReports.*;


public class TermMarksheetBuilder extends BaseReportBuilder {
    private final List<StudentResultBySubject> markList;

    private final List<StudentResultByLanguage> resultList;

    private final Period period;

    private final Classroom classroom;

    protected TermMarksheetBuilder(Classroom classroom, Period period, String lang) {
        super(PageType.A3, PageOrientation.LANDSCAPE);
        var subjectRepo = (StudentResultBySubjectRepository) Application.repositories.getRepositoryFor(StudentResultBySubject.class).get();
        var resultRepo = (StudentResultByLanguageRepository) Application.repositories.getRepositoryFor(StudentResultByLanguage.class).get();
        markList = subjectRepo.findByStudent_ClassroomAndClassroomSubject_Language(classroom, Language.valueOf(lang));
        resultList = resultRepo.findByStudent_ClassroomAndLanguage(classroom, Language.valueOf(lang));
        this.period = period;
        this.classroom = classroom;
        locale = Locale.forLanguageTag(lang);
    }

    @Override
    public HorizontalListBuilder createHeader() {
        var groupHeaderExpression = new AbstractSimpleExpression<String>() {
            @Override
            public String evaluate(ReportParameters params) {
                return params.getMessage("MS.class", new Object[]{classroom.toString()});
            }
        };
        return super.createHeader().newRow().add(cmp.text(groupHeaderExpression).setHeight(30)
                .setStyle(stl.style(Styles.boldCenteredStyle).setFontSize(16)));
    }

    @Override
    public BaseReportBuilder buildReport() {
        super.buildReport();
        titleField.setText(exp.message("MS.title", new Object[]{period.toString(locale).toUpperCase(), CURRENT_SCHOOL_YEAR}));

        var studentNameRowGroup = ctab.rowGroup("studentName", String.class)
                .setShowTotal(false)
                .setHeaderWidth(175)
                .setHeaderTextAdjust(Styles.defaultTextAdjust)
                .setHeaderStyle(stl.style(Styles.border1ptStyle));
        var subjectNameColGroup = ctab.columnGroup("subjectName", String.class)
                .setShowTotal(false)
                .setHeaderHeight(120)
                .setHeaderTextAdjust(Styles.defaultTextAdjust)
                .setHeaderStyle(stl.style(Styles.border1ptStyle).setRotation(Rotation.LEFT));
        var periodColGroup = ctab.columnGroup("period", String.class)
                .setShowTotal(false)
                .setHeaderTextAdjust(Styles.defaultTextAdjust)
                .setHeaderHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                .setHeaderStyle(stl.style(Styles.border1ptStyle));

        var markMeasure = ctab.measure("mark", String.class, Calculation.NOTHING)
                .setTextAdjust(Styles.defaultTextAdjust)
                .setStyle(stl.style(Styles.centered1ptBorderStyle));

        var crossTab = ctab.crosstab()
                .rowGroups(studentNameRowGroup)
                .columnGroups(periodColGroup, subjectNameColGroup)
                .setCellWidth(32)
                .measures(markMeasure);

        pageHeader(createHeader(), cmp.verticalGap(mm(5)))

                .summary(crossTab);

        return this;
    }

    @Override
    protected void configureMainDatasource() {
        super.configureMainDatasource();
        var ds = new DRDataSource("studentName", "period", "subjectName", "mark");
        markList.forEach(srbs -> {
            ds.add(srbs.getStudent().getFullName(),
                    isReportInFrench() ? "Mois 5" : "Month 5",
                    srbs.getClassroomSubject().getSubject().toString(locale),
                    formatDoubleNumber(srbs.getEval5Average()));
            ds.add(srbs.getStudent().getFullName(),
                    isReportInFrench() ? "Mois 6" : "Month 6",
                    srbs.getClassroomSubject().getSubject().toString(locale),
                    formatDoubleNumber(srbs.getEval6Average()));
        });
        resultList.forEach(sr -> {
            ds.add(sr.getStudent().getFullName(),
                    isReportInFrench() ? "Resume" : "Summary",
                    isReportInFrench() ? "Mois 5 moyenne" : "Month 5 average",
                    formatDoubleNumber(sr.getEval5Average()));
            ds.add(sr.getStudent().getFullName(),
                    isReportInFrench() ? "Resume" : "Summary",
                    isReportInFrench() ? "Mois 5 rang" : "Month 5 rank",
                    rankFormat(sr.getEval5Rank(), locale));
            ds.add(sr.getStudent().getFullName(),
                    isReportInFrench() ? "Resume" : "Summary",
                    isReportInFrench() ? "Mois 6 moyenne" : "Month 6 average",
                    formatDoubleNumber(sr.getEval6Average()));
            ds.add(sr.getStudent().getFullName(),
                    isReportInFrench() ? "Resume" : "Summary",
                    isReportInFrench() ? "Mois 6 rang" : "Month 6 rank",
                    rankFormat(sr.getEval6Rank(), locale));
            ds.add(sr.getStudent().getFullName(),
                    isReportInFrench() ? "Resume" : "Summary",
                    isReportInFrench() ? "Trim 3 moyenne" : "Term 3 average",
                    formatDoubleNumber(sr.getTerm3Average()));
            ds.add(sr.getStudent().getFullName(),
                    isReportInFrench() ? "Resume" : "Summary",
                    isReportInFrench() ? "Trim 3 rang" : "Term 3 rank",
                    rankFormat(sr.getTerm3Rank(), locale));
        });
        dataSource = ds;
    }
}
