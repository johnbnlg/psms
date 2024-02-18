package com.eunycesoft.psms.report;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.Constants;
import com.eunycesoft.psms.data.entity.Student;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.FieldBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.ImageBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.Markup;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.eunycesoft.psms.Constants.dateFormatter;
import static net.sf.dynamicreports.report.builder.DynamicReports.*;

public class BaseReportBuilder extends JasperReportBuilder {
    protected PageType pageType;
    protected PageOrientation orientation;
    protected ByteArrayOutputStream out = new ByteArrayOutputStream();
    protected JRDataSource dataSource = new JREmptyDataSource(1);
    private Connection connection;
    protected Locale locale = Constants.defaultLocale;
    protected int[] columnsWidths;
    protected String[] columnsTitleKeys;
    protected List<TextColumnBuilder<?>> columnsBuilders = new ArrayList<>();
    protected List<FieldBuilder<?>> FieldsBuilders = new ArrayList<>();

    protected TextFieldBuilder<String> titleField = cmp.text("")
            .setTextAdjust(Styles.defaultTextAdjust)
            .setStyle(Styles.A4PortraitReportTitleStyle);

    public BaseReportBuilder() {
        this(PageType.A4, PageOrientation.PORTRAIT);
    }

    protected BaseReportBuilder(PageType pageType, PageOrientation orientation) {
        this.pageType = pageType;
        this.orientation = orientation;
        var url = "jdbc:mysql://localhost:3306/psms2022?serverTimezone=UTC";
        var username = "psms";
        var password = "psms";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] exportToPdf() {
        try {
            toPdf(out);
        } catch (DRException e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }

    public BaseReportBuilder buildReport() {
        configureMainDatasource();
        setPageFormat(pageType, orientation)
                .setResourceBundle(ResourceBundle.getBundle("report", locale))
                .setPageMargin(margin(20))
                .pageFooter(createFooter())
                .background(createBackground())
                .setColumnStyle(Styles.centered1ptBorderStyle)
                .setColumnTitleStyle(Styles.reportCardBlueBg1ptBorderStyle)
                .setDataSource(dataSource)
                .setConnection(connection)
                .noData(cmp.text(exp.message("shared.no_data")).setStyle(Styles.boldCenteredStyle));
        return this;
    }

    protected void configureMainDatasource() {
    }

    private ImageBuilder createBackground() {
        Integer width, height;
        if (orientation == PageOrientation.PORTRAIT) {
            width = pageType.getWidth();
            height = pageType.getHeight();
        } else {
            width = pageType.getHeight();
            height = pageType.getWidth();
        }
        return cmp.image(Application.class.getResourceAsStream("/images/background.png")).setStyle(Styles.imageStyle)
                .setFixedDimension(width - cm(2), height - cm(2));

    }

    public HorizontalListBuilder createHeader() {
        int logoSize = (int) (pageType.getWidth() * 0.15);
        int fontSize = (int) (pageType.getWidth() * 0.0185);
        var textStyle = stl.style(Styles.centeredStyle).setFontSize(fontSize);
        return cmp.horizontalList(
                        cmp.text(exp.message("shared.left_header")).setTextAdjust(Styles.defaultTextAdjust).setStyle(textStyle),
                        cmp.image(getClass().getResourceAsStream("/images/logo.png")).setStyle(Styles.imageStyle).setFixedWidth(logoSize),
                        cmp.text(exp.message("shared.right_header")).setTextAdjust(Styles.defaultTextAdjust).setStyle(textStyle))
                .newRow().add(cmp.line().setPen(stl.pen1Point()))
                .newRow(5).add(titleField)
                .newRow(10);
    }

    public VerticalListBuilder createFooter() {
        return cmp.verticalList(
                cmp.line().setPen(stl.pen1Point()),
                cmp.text(exp.message("shared.page_footer")).setTextAdjust(Styles.defaultTextAdjust).setStyle(Styles.boldCenteredStyle),
                cmp.verticalGap(3),
                cmp.text("<i>Powered by EunyceSoft Corporation: 695 78 07 08 / 676 16 37 71</i>").setMarkup(Markup.HTML)
                        .setStyle(stl.style().setFontSize(6).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT))
        );
    }

    protected HorizontalListBuilder createDoneAtOn() {
        return cmp.horizontalList(
                cmp.text(""),
                cmp.text(exp.message("shared.done_at_on", new String[]{dateFormatter.format(LocalDate.now())}))
                        .setHeight(30)
                        .setStyle(Styles.centeredStyle.setFontSize(12))
        ).newRow(5);
    }

    protected HorizontalListBuilder createHeadMasterOnlySignature() {
        return cmp.horizontalList(
                cmp.text(""),
                cmp.text(exp.message("shared.the_headmaster"))
                        .setStyle(Styles.signaturePlaceStyle)
        );
    }

    protected HorizontalListBuilder createBurserOnlySignature() {
        return cmp.horizontalList(
                cmp.text(""),
                cmp.text(exp.message("shared.the_burser")).setStyle(Styles.signaturePlaceStyle)
        );
    }

    protected HorizontalListBuilder createBurserAndHeadMasterSignature() {
        return cmp.horizontalList(
                cmp.text(exp.message("shared.the_burser")).setStyle(Styles.signaturePlaceStyle),
                cmp.text(exp.message("shared.the_headmaster")).setStyle(Styles.signaturePlaceStyle)
        );
    }

    protected HorizontalListBuilder createTeacherAndHeadMasterSignature() {
        return cmp.horizontalList(
                cmp.text(exp.message("shared.the_teacher")).setStyle(Styles.signaturePlaceStyle),
                cmp.text(exp.message("shared.the_headmaster")).setStyle(Styles.signaturePlaceStyle)
        );
    }

    protected HorizontalListBuilder createTeacherParentAndHeadmasterSignature() {
        return cmp.horizontalList(
                cmp.text(exp.message("shared.the_teacher")).setStyle(Styles.signaturePlaceStyle),
                cmp.text(exp.message("shared.the_parent")).setStyle(Styles.signaturePlaceStyle),
                cmp.text(exp.message("shared.the_headmaster")).setStyle(Styles.signaturePlaceStyle)
        );
    }


    protected void configureColumnsBuilders(int[] colWidths, String[] colTitleKeys, List<TextColumnBuilder<?>> colBuilders) {
        if (colWidths == null) return;
        for (int i = 0; i < colBuilders.size(); i++) {
            colBuilders.get(i)
                    .setWidth(colWidths[i])
                    .setTitleTextAdjust(Styles.defaultTextAdjust)
                    .setTextAdjust(Styles.defaultTextAdjust);
            if (colTitleKeys[i] != null) colBuilders.get(i).setTitle(exp.message(colTitleKeys[i]));
        }
    }

    protected boolean isReportInFrench() {
        return locale.getLanguage().equals("fr");
    }

    protected String getBirthString(Student sdt) {
        if (sdt.getDateOfBirth() == null) return "";
        try {
            return MessageFormat.format(isReportInFrench() ? "{0, date, dd/MM/yyyy} à {1} " : "{0, date, dd/MM/yyyy} at {1} ",
                    sdt.getDateOfBirth(), sdt.getPlaceOfBirth());
        } catch (Exception e) {
            return "";
        }
    }

    protected String booleanToYesNo(boolean bool) {
        return bool ? isReportInFrench() ? "Oui" : "Yes"
                : isReportInFrench() ? "Non" : "No";
    }

    protected String appendLangSuffix(String fieldName) {
        return fieldName + (isReportInFrench() ? "Fr" : "En");
    }
}