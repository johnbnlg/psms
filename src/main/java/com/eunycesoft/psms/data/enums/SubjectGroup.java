package com.eunycesoft.psms.data.enums;

import com.eunycesoft.psms.Utils;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum SubjectGroup {
    LANG_COM(1, "Langues et communication", "Languages and communication"),
    SCI_TECH(2, "Sciences et technologies", "Sciences and Technologies"),
    HUM_SCI(3, "Sciences humaines et sociales", "Human and social sciences"),
    ART_CULT(4, "Education physique, artistique et culturelle", "Physical, artistic and cultural education");
    private final int id;
    private final String nameFr, nameEn;

    SubjectGroup(int id, String nameFr, String nameEn) {
        this.id = id;
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
