package com.eunycesoft.psms.data.enums;

import com.eunycesoft.psms.Utils;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Getter
public enum Period {
    M1(1, "Premier mois", "First month"),
    M2(2, "Deuxième mois", "Second month"),
    M3(3, "Troisième mois", "Third month"),
    M4(4, "Quatrième mois", "Fourth month"),
    M5(5, "Cinquième mois", "Fifth month"),
    M6(6, "Sixième mois", "Sixth month"),
    T1(7, "Premier trimestre", "First term"),
    T2(8, "Deuxième trimestre", "Second term"),
    T3(9, "Troisième trimestre", "Third term"),
    AN(10, "Année entière", "Whole year");
    private final int id;
    private final String nameFr, nameEn;

    Period(int id, String nameFr, String nameEn) {
        this.id = id;
        this.nameFr = nameFr;
        this.nameEn = nameEn;
    }

    public static List<Period> getMonths() {
        return Arrays.stream(values()).filter(p -> p.name().startsWith("M")).collect(Collectors.toList());
    }

    public static List<Period> getTerms() {
        return Arrays.asList(T1, T2, T3, AN);
    }

    @Override
    public String toString() {
        return (Utils.getSessionLanguage().equals("en") ? nameEn : nameFr);
    }

    public String toString(Locale locale) {
        return (locale.getLanguage().equals("en") ? nameEn : nameFr);
    }

    public String getAvgColumn() {
        return "eval" + id + "average";
    }

    public String getAppColumn() {
        return "eval" + id + "appreciation";
    }

    public String getTotalColumn() {
        return "eval" + id + "total";
    }

    public String getOverColumn() {
        return "eval" + id + "over";
    }
}
