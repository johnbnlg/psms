package com.eunycesoft.psms.data.enums;

import com.eunycesoft.psms.Utils;
import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;

@Getter
public enum Appreciation {
    NOT_MEETING(0, 9.9999, "N", "NYE", "Non acquis", "Not yet meeting Expectation"),
    APPROACHING(10, 14.9999, "B", "AE", "En cours d'acquisition", "Approaching Expectation"),
    MEETING(15, 17.9999, "A", "ME", "Acquis", "Meeting Expectation"),
    ABOVE(18, 20, "A+", "AE+", "Expert", "Above Expectation");
    private final double min, max;
    private final String codeFr, codeEn, nameFr, nameEn;

    Appreciation(double min, double max, String codeFr, String codeEn, String nameFr, String nameEn) {
        this.min = min;
        this.max = max;
        this.codeFr = codeFr;
        this.codeEn = codeEn;
        this.nameFr = nameFr;
        this.nameEn = nameEn;
    }

    public static String appreciate(Double average, Double over, Language language) {
        if (average == null) return null;
        Double avgOver20 = average * 20 / over;
        var appreciation = Arrays.stream(Appreciation.values()).filter(app -> app.getMin() <= average && app.getMax() >= avgOver20).findFirst().get();
        return (language == null) ? (appreciation.getCodeFr() + "/" + appreciation.getCodeEn())
                : language == Language.FR ? appreciation.codeFr : appreciation.codeEn;
    }

    @Override
    public String toString() {
        return (Utils.getSessionLanguage().equals("en") ? codeEn : codeFr);
    }

    public String toString(Locale locale) {
        return (locale.getLanguage().equals("en") ? codeEn : codeFr);
    }
}
