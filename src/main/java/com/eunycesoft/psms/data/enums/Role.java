package com.eunycesoft.psms.data.enums;

import com.eunycesoft.psms.Utils;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum Role {
    ADMIN("Administrateur", "Administrator"),
    HEADMASTER("Directeur", "Headmaster"),
    BURSER("Économe", "Burser"),
    SECRETARY("Secrétaire", "Secretary"),
    DISCIPLINE_MASTER("Surveillant(e)", "Discipline master"),
    TEACHER("Enseignant(e)", "Teacher"),
    GUIDANCE_COUNSELOR("Conseiller d'orientation", "Guidance counselor"),
    NURSE("Infirmière", "Nurse"),
    DRIVER("Chauffeur", "Driver"),
    WATCHMAN("Gardien", "Watchman"),
    STUDENT("Élève", "Student");
    private final String nameFr, nameEn;

    Role(String nameFr, String nameEn) {
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
