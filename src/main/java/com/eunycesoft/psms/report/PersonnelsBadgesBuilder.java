package com.eunycesoft.psms.report;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.Constants;
import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.entity.Personnel;
import com.eunycesoft.psms.data.repository.PersonnelRepository;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.constant.ImageScale;
import net.sf.dynamicreports.report.constant.LineStyle;
import net.sf.dynamicreports.report.constant.SplitType;
import net.sf.dynamicreports.report.constant.TextAdjust;
import org.springframework.data.domain.Sort;

import java.awt.*;
import java.util.List;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

public class PersonnelsBadgesBuilder extends BaseReportBuilder {

    private List<Personnel> list;

    protected PersonnelsBadgesBuilder() {
        super();
        var repo = (PersonnelRepository) Application.repositories.getRepositoryFor(Personnel.class).get();
        list = repo.findAll(Sort.by("name", "surname"));
    }

    @Override
    public BaseReportBuilder buildReport() {
        setNoDataSplitType(SplitType.IMMEDIATE);
        var background = cmp.image(getClass().getResourceAsStream("/images/personnel-badge.png"));
        HorizontalListBuilder line = null;
        var color = new Color(0, 180, 240);
        for (int i = 0; i < list.size(); i++) {
            var pers = list.get(i);
            var name = cmp.text(pers.getName());
            var surname = cmp.text(pers.getSurname());
            var role = pers.getMainRole();
            var lang = pers.getLanguage().getTag();
            var quality = cmp.text(lang.equalsIgnoreCase("fr") ? role.getNameFr() : role.getNameEn());
            var schoolYear = cmp.text(Constants.CURRENT_SCHOOL_YEAR);
            List.of(name, surname).forEach(field -> {
                field.setStyle(stl.style(Styles.boldStyle)
                                .setForegroundColor(color)
                                .setFontSize(8))
                        .setTextAdjust(TextAdjust.SCALE_FONT);
            });
            List.of(quality, schoolYear).forEach(field -> {
                field.setStyle(stl.style(Styles.boldStyle)
                                .setForegroundColor(Color.RED)
                                .setFontSize(8))
                        .setTextAdjust(TextAdjust.SCALE_FONT);
            });
            var card = cmp.verticalList(
                            cmp.filler().setFixedHeight(mm(27)),
                            cmp.horizontalList(
                                    cmp.verticalList(
                                                    cmp.filler(),
                                                    cmp.image(getClass().getResourceAsStream("/images/flag.png"))
                                                            .setImageScale(ImageScale.RETAIN_SHAPE)
                                                            .setFixedDimension(35, 35)
                                            ).setFixedWidth(mm(30))
                                            .setBackgroundComponent(cmp.image(Utils.getPhotoAsInputStream(pers)).setStyle(Styles.imageStyle)),
                                    cmp.filler(),
                                    cmp.verticalList(name, surname, quality, schoolYear).setFixedWidth(mm(37))
                            ).setFixedHeight(mm(27))
                    ).setFixedDimension(mm(86), mm(54))
                    .setStyle(stl.style().setBorder(stl.pen(0.25f, LineStyle.SOLID)))
                    .setBackgroundComponent(background.setImageScale(ImageScale.FILL_FRAME));
            if (i % 2 == 0) {
                line = cmp.horizontalList(cmp.horizontalGap(40), card, cmp.horizontalGap(10));
                addNoData(line);
            } else {
                line.add(card);
                addNoData(cmp.verticalGap(10));
            }
        }
        return this;
    }
}
