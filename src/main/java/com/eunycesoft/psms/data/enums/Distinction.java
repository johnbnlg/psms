package com.eunycesoft.psms.data.enums;

import com.eunycesoft.psms.Utils;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum Distinction {
    HR(12, 13.9999, "T.H.", "H.R.", "Tableau d'honneur", "Honour roll"),
    HR_ENC(14, 15.9999, "T.H. + ENC.", "H.R. + ENC.", "Tableau d'honneur + encouragements", "Honour roll + encouragement"),
    HR_CONG(16, 20, "T.H. + FEL.", "H.R. + CONG.", "Tableau d'honneur + félicitation", "Honour roll + congratulation");
    private final double min, max;
    private final String codeFr, codeEn, nameFr, nameEn;

    Distinction(double min, double max, String codeFr, String codeEn, String nameFr, String nameEn) {
        this.min = min;
        this.max = max;
        this.codeFr = codeFr;
        this.codeEn = codeEn;
        this.nameFr = nameFr;
        this.nameEn = nameEn;
    }

    @Override
    public String toString() {
        return (Utils.getSessionLanguage().equals("en") ? codeEn : codeFr);
    }

    public String toString(Locale locale) {
        return (locale.getLanguage().equals("en") ? codeEn : codeFr);
    }
}
