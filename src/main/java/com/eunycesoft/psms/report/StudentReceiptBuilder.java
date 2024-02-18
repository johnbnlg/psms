package com.eunycesoft.psms.report;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.data.enums.Fee;
import com.eunycesoft.psms.data.repository.StudentRepository;
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.constant.*;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.definition.ReportParameters;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.eunycesoft.psms.Constants.CURRENT_SCHOOL_YEAR;
import static com.eunycesoft.psms.Constants.dateFormatter;
import static net.sf.dynamicreports.report.builder.DynamicReports.*;


public class StudentReceiptBuilder extends BaseReportBuilder {

    private final List<Fee> fees;
    private Student student;
    private int totalNetToPay, totalPaid, totalLeftToPay;
    private String studentName;
    private String className;

    protected StudentReceiptBuilder(Integer studentId, List<Fee> fees) {
        super(PageType.A5, PageOrientation.PORTRAIT);
        this.fees = fees;
        var repo = (StudentRepository) Application.repositories.getRepositoryFor(Student.class).get();
        student = repo.findById(studentId).get();
        locale = new Locale(student.getLanguage().getTag());
    }

    @Override
    public BaseReportBuilder buildReport() {
        super.buildReport();

        titleField.setText(exp.message("SR.title"));

        columnsWidths = new int[]{140, 65, 65, 100};
        columnsTitleKeys = new String[]{"SR.fee_entitled", "SR.paid_amount", "SR.payment_date", "SR.operator_name"};
        columnsBuilders = List.of(
                col.column("feeName", String.class),
                col.column("paymentAmount", type.integerType()),
                col.column("paymentDate", type.stringType()),
                col.column("operatorName", type.stringType())
        );

        configureColumnsBuilders(columnsWidths, columnsTitleKeys, columnsBuilders);
        columnsBuilders.get(0).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

        var feeGroup = grp.group("feeName", String.class)
                .setHeaderLayout(GroupHeaderLayout.EMPTY)
                .setPadding(0);
        var groupNetTay = variable("groupNetToTay", "netToPay", Integer.class, Calculation.NOTHING)
                .setResetGroup(feeGroup);
        var groupTotalPaid = variable("groupTotalPaid", "paymentAmount", Integer.class, Calculation.SUM)
                .setResetGroup(feeGroup);

        var groupFooterExpression = new AbstractSimpleExpression<String>() {
            @Override
            public String evaluate(ReportParameters params) {
                var groupNetTay = (int) params.getVariableValue("groupNetToTay");
                var groupTotalPaid = (int) params.getVariableValue("groupTotalPaid");
                var dateline = (String) params.getFieldValue("dateline");
                return params.getMessage("SR.fee_summary", new Object[]{groupTotalPaid, groupNetTay - groupTotalPaid, dateline});
            }
        };

        var globalFooterExpression = exp.message("SR.global_summary", new Object[]{totalNetToPay, totalPaid, totalLeftToPay});
        var headerInformationsExpression = exp.message("SR.header_informations", new Object[]{studentName, className, CURRENT_SCHOOL_YEAR});

        setDefaultFont(stl.font().setFontSize(10))
                .pageHeader(createHeader(), cmp.text(headerInformationsExpression).setStyle(Styles.centeredStyle))
                .fields(field("dateline", type.dateType()))
                .columns(columnsBuilders.toArray(new TextColumnBuilder[0]))
                .variables(groupNetTay, groupTotalPaid)
                .groupBy(feeGroup)
                .groupFooter(feeGroup, cmp.text(groupFooterExpression).setTextAdjust(TextAdjust.SCALE_FONT).setHeight(20).setStyle(Styles.centeredStyle))
                .summary(cmp.text(globalFooterExpression).setTextAdjust(TextAdjust.SCALE_FONT).setHeight(35).setStyle(Styles.centeredStyle))
                .addSummary(createDoneAtOn().add(createBurserOnlySignature()));

        return this;
    }

    @Override
    protected void configureMainDatasource() {
        totalNetToPay = totalPaid = totalLeftToPay = 0;
        var studentFees = student.getStudentFees().stream()
                .filter(sf -> fees.contains(sf.getFee()));
        var studentPayments = student.getStudentPayments();
        var ds = new DRDataSource("feeName", "dateline", "netToPay", "paymentAmount", "paymentDate", "operatorName");
        studentFees.forEach(sf -> {
            totalNetToPay += sf.getNetToPay();
            var feeName = isReportInFrench() ? sf.getFee().getNameFr() : sf.getFee().getNameEn();
            var dateline = dateFormatter.format(sf.getDateline());
            var payments = studentPayments.stream()
                    .filter(sp -> sp.getFee().equals(sf.getFee()))
                    .collect(Collectors.toList());
            if (payments.isEmpty()) {
                ds.add(feeName, dateline, sf.getNetToPay(), 0, null, "");
            } else {
                payments.forEach(sp -> {
                    ds.add(feeName,
                            dateline,
                            sf.getNetToPay(),
                            sp.getAmount(),
                            dateFormatter.format(sp.getPaymentDate()),
                            sp.getOperator().getCivilName());
                    totalPaid += sp.getAmount();
                });
            }
        });
        totalLeftToPay = totalNetToPay - totalPaid;
        studentName = student.getFullName();
        className = student.getClassroom().getName();
        dataSource = ds;
    }
}
