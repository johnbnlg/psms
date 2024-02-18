package com.eunycesoft.psms.report;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.enums.Fee;
import com.eunycesoft.psms.data.enums.FinancialReportGranularity;
import com.eunycesoft.psms.data.enums.Section;
import com.eunycesoft.psms.data.repository.StudentRepository;
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.group.GroupBuilder;
import net.sf.dynamicreports.report.builder.subtotal.AggregationSubtotalBuilder;
import net.sf.dynamicreports.report.constant.Calculation;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.definition.ReportParameters;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.eunycesoft.psms.data.enums.FinancialReportGranularity.*;
import static net.sf.dynamicreports.report.builder.DynamicReports.*;

public class FinancialReportBuilder extends BaseReportBuilder {
    private final FinancialReportGranularity granularity;
    private final List<Fee> fees;
    private String feesString;
    private TextColumnBuilder<Integer> numberColumn;
    private TextColumnBuilder<String> granularityColumn;

    private String granularityColumnKey;
    private GroupBuilder groupBy;
    private final TextColumnBuilder<String> busLineColumn = col.column("busLine", type.stringType());
    private final TextColumnBuilder<Integer> rawToPayColumn = col.column("rawToPay", type.integerType());
    private final TextColumnBuilder<Integer> discountColumn = col.column("discount", type.integerType());
    private final TextColumnBuilder<Integer> netToPayColumn = col.column("netToPay", type.integerType());
    private final TextColumnBuilder<Integer> paidColumn = col.column("paid", type.integerType());
    private final TextColumnBuilder<Integer> leftToPayColumn = col.column("leftToPay", type.integerType());

    protected FinancialReportBuilder(FinancialReportGranularity granularity, List<Fee> fees, String lang) {
        super();
        this.granularity = granularity;
        this.fees = Arrays.stream(Fee.values()).filter(fee -> fees.contains(fee)).collect(Collectors.toList());
        feesString = this.fees.stream().map(fee -> (isReportInFrench() ? fee.getNameFr() : fee.getNameEn()))
                .collect(Collectors.joining(", "));
    }

    @Override
    public HorizontalListBuilder createHeader() {
        return super.createHeader().newRow().add(cmp.text(exp.message("FS.header_informations", new Object[]{feesString}))
                .setHeight(30).setStyle(Styles.boldCenteredStyle));
    }

    @Override
    public BaseReportBuilder buildReport() {
        super.buildReport();
        var numericColumns = Arrays.asList(rawToPayColumn, discountColumn, netToPayColumn, paidColumn, leftToPayColumn);
        var groupSubToTals = numericColumns.stream().map(column -> sbt.sum(column)).collect(Collectors.toList());
        var globalSubToTals = numericColumns.stream().map(column -> sbt.sum(column)).collect(Collectors.toList());
        var groupNetToPaySum = variable(netToPayColumn, Calculation.SUM);
        var globalNetToPaySum = variable(netToPayColumn, Calculation.SUM);
        var groupPaidSum = variable(paidColumn, Calculation.SUM);
        var globalPaidSum = variable(paidColumn, Calculation.SUM);

        var groupRecoveryRateExpression = new AbstractSimpleExpression<String>() {
            @Override
            public String evaluate(ReportParameters params) {
                int gntps = params.getVariableValue(groupNetToPaySum.getName());
                int gps = params.getVariableValue(groupPaidSum.getName());
                return params.getMessage("FS.partial_recovery_rate", new Object[]{100.0 * gps / gntps});
            }
        };
        var globalRecoveryRateExpression = new AbstractSimpleExpression<String>() {
            @Override
            public String evaluate(ReportParameters params) {
                int gntps = params.getVariableValue(globalNetToPaySum.getName());
                int gps = params.getVariableValue(globalPaidSum.getName());
                return params.getMessage("FS.global_recovery_rate", new Object[]{100.0 * gps / gntps});
            }
        };

        titleField.setText(exp.message(granularity.getTitleKey()));
        granularityColumn = col.column(granularity.getGranularityColumn(), type.stringType());
        granularityColumnKey = granularity.getGranularityColumnKey();
        numberColumn = col.column(exp.pageRowNumber());

        if (granularity.equals(INSOLVENT) || granularity.equals(STUDENT) || granularity.equals(CLASSROOM)) {
            groupBy = grp.group(granularity.getGroupColumn(), String.class)
                    .setPadding(0);
            numberColumn = col.column(exp.groupRowNumber(groupBy)).setPattern("00").setTitle("N<sup>o</sup>");
            groupNetToPaySum.setResetGroup(groupBy);
            groupPaidSum.setResetGroup(groupBy);
            groupBy.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                    .setStyle(Styles.boldCenteredStyle);
            groupBy(groupBy).subtotalsAtGroupFooter(groupBy, groupSubToTals.toArray(new AggregationSubtotalBuilder[0]))
                    .addSubtotalAtGroupFooter(groupBy, sbt.customValue(groupRecoveryRateExpression, granularityColumn))
                    .addSubtotalAtGroupFooter(groupBy, sbt.customValue(exp.text("X"), numberColumn))
                    .variables(groupNetToPaySum, groupPaidSum);
        }
        if ((granularity.equals(INSOLVENT) || granularity.equals(STUDENT) && fees.contains(Fee.TRANS))){
            columnsWidths = new int[]{20, 200, 50, 55, 55, 55, 55, 55};
            columnsTitleKeys = new String[]{null, granularityColumnKey, "FS.bus_line","FS.amount", "FS.discount", "FS.net_to_pay", "FS.already_paid", "FS.left_to_pay"};
            columnsBuilders = Arrays.asList(numberColumn, granularityColumn, busLineColumn,rawToPayColumn, discountColumn, netToPayColumn, paidColumn, leftToPayColumn);

            subtotalsAtGroupFooter(groupBy, sbt.customValue(exp.text(""), busLineColumn));
            addSubtotalAtSummary(sbt.customValue(exp.text(""), busLineColumn));
        }else{
            columnsWidths = new int[]{20, 200, 65, 65, 65, 65, 65};
            columnsTitleKeys = new String[]{null, granularityColumnKey, "FS.amount", "FS.discount", "FS.net_to_pay", "FS.already_paid", "FS.left_to_pay"};
            columnsBuilders = Arrays.asList(numberColumn, granularityColumn, rawToPayColumn, discountColumn, netToPayColumn, paidColumn, leftToPayColumn);
        }
        configureColumnsBuilders(columnsWidths, columnsTitleKeys, columnsBuilders);
        columnsBuilders.get(1).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);

        pageHeader(createHeader().setPrintWhenExpression(exp.printInFirstPage()).newRow(10))
                .variables(globalNetToPaySum, globalPaidSum)
                .subtotalsAtSummary(globalSubToTals.toArray(new AggregationSubtotalBuilder[0]))
                .addSubtotalAtSummary(sbt.customValue(globalRecoveryRateExpression, granularityColumn))
                .addSubtotalAtSummary(sbt.customValue(exp.text("X"), numberColumn))
                .setSubtotalStyle(stl.style(Styles.boldCentered1ptBorderStyle).setFontSize(10))
                .columns(columnsBuilders.toArray(new TextColumnBuilder[0]))
                .summary(createDoneAtOn().add(createBurserAndHeadMasterSignature()));
        return this;
    }

    @Override
    protected void configureMainDatasource() {
        switch (granularity) {
            case STUDENT, INSOLVENT -> {
                var repo = (StudentRepository) Application.repositories.getRepositoryFor(Student.class).get();
                var ds = new DRDataSource("section", "classroom", "student", "busLine","rawToPay", "discount", "netToPay", "paid", "leftToPay");
                var list = repo.findAll();
                list.sort(Comparator.<Student>comparingInt(sdt -> sdt.getClassroom().getId())
                        .thenComparing(Student::getFullName));
                list.forEach(sdt -> {
                    var leftToPay = sdt.getLeftToPay(fees);
                    var rawToPay = sdt.getRawAmountToPay(fees);
                    if (granularity.equals(INSOLVENT) && leftToPay == 0) return;
                    if (fees.size() == 1 && fees.get(0) == Fee.TRANS && rawToPay == 0) return;
                    var discount = sdt.getDiscount(fees);
                    var paid = sdt.getTotalPaid(fees);
                    var netToPay = rawToPay - discount;
                    var sectionName = isReportInFrench()
                            ? sdt.getClassroom().getSection().getNameFr()
                            : sdt.getClassroom().getSection().getNameEn();
                    ds.add("Section " + sectionName, sdt.getClassroom().getName(), sdt.getFullName(),  Objects.toString(sdt.getBusLine(), ""),rawToPay, discount, netToPay, paid, leftToPay);
                });
                dataSource = ds;
            }
            case CLASSROOM -> {
                var ds = new DRDataSource("section", "classroom", "rawToPay", "discount", "netToPay", "paid", "leftToPay");
                Arrays.stream(Classroom.values()).forEach(cls -> {
                    var rawToPay = cls.getStudents().stream().mapToInt(sdt -> sdt.getRawAmountToPay(fees)).sum();
                    var discount = cls.getStudents().stream().mapToInt(sdt -> sdt.getDiscount(fees)).sum();
                    var paid = cls.getStudents().stream().mapToInt(sdt -> sdt.getTotalPaid(fees)).sum();
                    var netToPay = rawToPay - discount;
                    var leftToPay = netToPay - paid;
                    var sectionName = isReportInFrench() ? cls.getSection().getNameFr() : cls.getSection().getNameEn();
                    ds.add("Section " + sectionName, cls.getName(), rawToPay, discount, netToPay, paid, leftToPay);
                });
                dataSource = ds;
            }

            case SECTION -> {
                var ds = new DRDataSource("section", "rawToPay", "discount", "netToPay", "paid", "leftToPay");
                Arrays.stream(Section.values()).forEach(sec -> {
                    var classrooms = sec.getClassrooms();
                    var rawToPay = classrooms.stream()
                            .mapToInt(cls -> cls.getStudents().stream().mapToInt(sdt -> sdt.getRawAmountToPay(fees)).sum())
                            .sum();
                    var discount = classrooms.stream()
                            .mapToInt(cls -> cls.getStudents().stream().mapToInt(sdt -> sdt.getDiscount(fees)).sum())
                            .sum();
                    var paid = classrooms.stream()
                            .mapToInt(cls -> cls.getStudents().stream().mapToInt(sdt -> sdt.getTotalPaid(fees)).sum())
                            .sum();
                    var netToPay = rawToPay - discount;
                    var leftToPay = netToPay - paid;
                    var sectionName = isReportInFrench() ? sec.getNameFr() : sec.getNameEn();
                    ds.add("Section " + sectionName, rawToPay, discount, netToPay, paid, leftToPay);
                });
                dataSource = ds;
            }
        }
    }
}


