package com.eunycesoft.psms.data.enums;

import com.eunycesoft.psms.Utils;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Locale;

@Getter
public enum Fee {
    REG(1, "Inscription", "Registration", LocalDate.parse("2022-10-10")),
    SLIDE1(2, "Première tranche", "First slide", LocalDate.parse("2022-10-10")),
    SLIDE2(3, "Deuxième tranche", "Second slide", LocalDate.parse("2023-01-10")),
    TRANS(4, "Transport", "Transport", LocalDate.parse("2023-01-10")),
    ENT_SIX(7, "Entrée en sixième", "Entrée en sixième", LocalDate.parse("2023-11-04")),
    COM_ENT(8, "Common entrance", "Common entrance", LocalDate.parse("2023-11-04")),
    CEP(5, "CEP", "CEP", LocalDate.parse("2023-11-04")),
    FSLC(6, "FSLC", "FSLC", LocalDate.parse("2023-11-04"));

    private final int id;
    private final String nameFr, nameEn;
    private final LocalDate dateline;

    Fee(int id, String nameFr, String nameEn, LocalDate dateline) {
        this.id = id;
        this.nameFr = nameFr;
        this.nameEn = nameEn;
        this.dateline = dateline;
    }

    @Override
    public String toString() {
        return (Utils.getSessionLanguage().equals("en") ? nameEn : nameFr);
    }

    public String toString(Locale locale) {
        return (locale.getLanguage().equals("en") ? nameEn : nameFr);
    }
}
