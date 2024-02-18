package com.eunycesoft.psms.data.enums;

import com.eunycesoft.psms.Utils;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum Cycle {
    MAT("Maternelle", "Nursery"),
    PRIM("Primaire", "Primary");
    private final String nameFr, nameEn;

    Cycle(String nameFr, String nameEn) {
        this.nameFr = nameFr;
        this.nameEn = nameEn;
    }

    @Override
    public String toString() {
        return (Utils.getSessionLanguage().equals("en") ? nameEn : nameFr);
    }

    public String toString(Locale locale) {
        return (locale.getLanguage().equals("en") ? nameEn : nameFr);
    }
}
