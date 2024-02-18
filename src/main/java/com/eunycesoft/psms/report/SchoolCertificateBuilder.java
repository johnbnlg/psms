package com.eunycesoft.psms.report;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.data.repository.StudentRepository;
import net.sf.dynamicreports.report.constant.*;

import java.util.Locale;
import java.util.Objects;

import static com.eunycesoft.psms.Constants.CURRENT_SCHOOL_YEAR;
import static com.eunycesoft.psms.Constants.dateFormatter;
import static net.sf.dynamicreports.report.builder.DynamicReports.*;


public class SchoolCertificateBuilder extends BaseReportBuilder {
    private Student student;

    protected SchoolCertificateBuilder(Integer studentId) {
        super();
        var repo = (StudentRepository) Application.repositories.getRepositoryFor(Student.class).get();
        student = repo.findById(studentId).get();
        locale = new Locale(student.getLanguage().getTag());
    }

    @Override
    public BaseReportBuilder buildReport() {
        super.buildReport();
        titleField.setText(exp.message("SC.title"));
        pageHeader(createHeader(), cmp.verticalGap(mm(5)))
                .detail(cmp.text(exp.message("SC.body", new Object[]{
                                        student.getFullName(),
                                        (student.getDateOfBirth() == null) ? "" : dateFormatter.format(student.getDateOfBirth()),
                                        Objects.toString(student.getPlaceOfBirth(), "N/A"),
                                        student.getGender().toString(locale),
                                        Objects.toString(student.getFatherName(), "N/A"),
                                        Objects.toString(student.getMotherName(), "N/A"),
                                        student.getRegistrationNumber(),
                                        student.getClassroom().getName(),
                                        CURRENT_SCHOOL_YEAR
                                })).setTextAdjust(TextAdjust.SCALE_FONT)
                                .setHorizontalTextAlignment(HorizontalTextAlignment.JUSTIFIED)
                                .setHeight(300)
                                .setStyle(stl.style().setFontSize(14).setMarkup(Markup.HTML).setLineSpacing(LineSpacing.DOUBLE)),
                        createDoneAtOn().add(createHeadMasterOnlySignature()),
                        cmp.pageBreak()
                );
        return this;
    }
}
