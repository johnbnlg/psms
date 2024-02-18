package com.eunycesoft.psms.report;

import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.*;

import java.awt.*;

import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

public class Styles {
    public static final StyleBuilder rootStyle = stl.style().setFontSize(10).setPadding(2).setMarkup(Markup.HTML).setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
    public static final StyleBuilder centeredStyle = stl.style(rootStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
    public static final StyleBuilder boldCenteredStyle = stl.style(centeredStyle).bold();
    public static final StyleBuilder imageStyle = stl.style().setImageScale(ImageScale.RETAIN_SHAPE).setImageAlignment(HorizontalImageAlignment.CENTER, VerticalImageAlignment.MIDDLE);
    public static final StyleBuilder image1ptBorderStyle = stl.style(imageStyle).setBorder(stl.pen1Point());
    public static final StyleBuilder boldCenteredItalicStyle = stl.style(boldCenteredStyle).italic();
    public static final StyleBuilder boldCenteredItalic1ptBorderStyle = stl.style(boldCenteredItalicStyle).setBorder(stl.pen1Point());
    public static final StyleBuilder boldCentered1ptBorderStyle = stl.style(boldCenteredStyle).setBorder(stl.pen1Point());
    public static final StyleBuilder reportCardBlueBg1ptBorderStyle = stl.style(boldCentered1ptBorderStyle).setBackgroundColor(new Color(0, 180, 240));
    public static final StyleBuilder reportCardColumnTitleStyle = stl.style(reportCardBlueBg1ptBorderStyle);
    public static final StyleBuilder reportCardGreenBg1ptBorderStyle = stl.style(boldCentered1ptBorderStyle).setBackgroundColor(new Color(150, 180, 90));
    public static final StyleBuilder reportCardSubjectGroupHeaderStyle = stl.style(reportCardGreenBg1ptBorderStyle);
    public static final StyleBuilder reportCardSubjectGroupFooterStyle = stl.style(boldCentered1ptBorderStyle).setFontSize(10);
    public static final StyleBuilder reportCardBlueBgStyle = stl.style(boldCenteredStyle).setBackgroundColor(new Color(0, 180, 240));
    public static final StyleBuilder baseReportTitleStyle = stl.style(reportCardBlueBgStyle).setFontSize(16);
    public static final StyleBuilder A4PortraitReportTitleStyle = baseReportTitleStyle;
    public static final StyleBuilder reportCardGreenBgStyle = stl.style(boldCenteredStyle).setBackgroundColor(new Color(150, 180, 90));
    public static final StyleBuilder signaturePlaceStyle = boldCenteredStyle;
    public static final StyleBuilder centeredItalicStyle = stl.style(centeredStyle).italic();
    public static final StyleBuilder centeredItalic1ptBorderStyle = stl.style(centeredItalicStyle).setBorder(stl.pen1Point());
    public static final StyleBuilder centered1ptBorderStyle = stl.style(centeredStyle).setBorder(stl.pen1Point());
    public static final StyleBuilder boldStyle = stl.style(rootStyle).bold();
    public static final StyleBuilder boldItalicStyle = stl.style(boldStyle).italic();
    public static final StyleBuilder boldItalic1ptBorderStyle = stl.style(boldItalicStyle).setBorder(stl.pen1Point());
    public static final StyleBuilder bold1ptBorderStyle = stl.style(boldStyle).setBorder(stl.pen1Point());
    public static final StyleBuilder italicStyle = stl.style(rootStyle).italic();
    public static final StyleBuilder italic1ptBorderStyle = stl.style(italicStyle).setBorder(stl.pen1Point());
    public static final StyleBuilder border1ptStyle = stl.style(rootStyle).setBorder(stl.pen1Point());
    public static final TextAdjust defaultTextAdjust = TextAdjust.SCALE_FONT;
}
