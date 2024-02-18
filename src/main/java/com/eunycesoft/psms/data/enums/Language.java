package com.eunycesoft.psms.data.enums;

import com.eunycesoft.psms.Utils;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum Language {
    FR("Français", "French"),
    EN("Anglais", "English");
    private final String nameFr, nameEn;

    Language(String nameFr, String nameEn) {
        this.nameFr = nameFr;
        this.nameEn = nameEn;
    }

    public String getTag() {
        return name().toLowerCase();
    }

    @Override
    public String toString() {
        return (Utils.getSessionLanguage().equals("en") ? nameEn : nameFr);
    }

    public String toString(Locale locale) {
        return (locale.getLanguage().equals("en") ? nameEn : nameFr);
    }
}
