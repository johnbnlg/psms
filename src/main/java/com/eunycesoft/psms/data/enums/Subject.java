package com.eunycesoft.psms.data.enums;

import com.eunycesoft.psms.Utils;
import lombok.Getter;

import java.util.Locale;

import static com.eunycesoft.psms.data.enums.SubjectGroup.*;

@Getter
public enum Subject {
    SPEAK(1, "Expression orale", "Speaking", LANG_COM, 0.5, 0.3, 0.1, 0.1),
    COM_FRE(2, "Communiquer en français", "Communicate in french language", LANG_COM, 0.3, 0.5, 0.1, 0.1),
    COM_ENG(3, "Communiquer en  anglais", "Communicate in english language", LANG_COM, 0.3, 0.5, 0.1, 0.1),
    MATH(4, "Notions de base en mathématiques", "Basic notions in mathematics", SCI_TECH, 0.3, 0.5, 0.1, 0.1),
    TECH(5, "Notions de base en sciences et tech.", "Basic notions in science and technologies", SCI_TECH, 0.3, 0.5, 0.1, 0.1),
    ICT(6, "Utiliser les concepts de base et les outils TIC", "Use basic ICT concepts and tools", SCI_TECH, 0.1, 0.3, 0.5, 0.1),
    HG(7, "Pratiquer les valeurs sociales (hist-géo)", "Practice social values (hist-geo)", HUM_SCI, 0.1, 0.5, 0.1, 0.3),
    MORAL(8, "Pratiquer les valeurs citoyennes", "Practice citizenship values", HUM_SCI, 0.1, 0.5, 0.1, 0.3),
    NAT_LANG(9, "Pratiquer une langue nationale", "Communicate in national languages", ART_CULT, 0.5, 0.3, 0.1, 0.1),
    SPORT(10, "Pratiquer les activités physiques et sportives", "Practice physical and sports activities", ART_CULT, 0.1, 0.3, 0.5, 0.1),
    ENTREP(11, "Culture d’autonomie et d'entrepreneuriat.", "Autonomy and entrepreneurship", ART_CULT, 0.1, 0.3, 0.5, 0.1),
    ART(12, "Pratiquer les activités artistiques", "Practice artistic education", ART_CULT, 0.1, 0.3, 0.5, 0.1);
    private final int id;
    private final String nameFr, nameEn;
    private final SubjectGroup subjectGroup;
    private final Double defaultOralRatio, defaultWrittenRatio, defaultPracticalRatio, defaultAttitudeRatio;

    Subject(int id, String nameFr, String nameEn, SubjectGroup subjectGroup,
            Double defaultOralRatio, Double defaultWrittenRatio, Double defaultPracticalRatio, Double defaultAttitudeRatio) {
        this.id = id;
        this.nameFr = nameFr;
        this.nameEn = nameEn;
        this.subjectGroup = subjectGroup;
        this.defaultOralRatio = defaultOralRatio;
        this.defaultWrittenRatio = defaultWrittenRatio;
        this.defaultPracticalRatio = defaultPracticalRatio;
        this.defaultAttitudeRatio = defaultAttitudeRatio;
    }

    @Override
    public String toString() {
        return (Utils.getSessionLanguage().equals("en") ? nameEn : nameFr);
    }

    public String toString(Locale locale) {
        return (locale.getLanguage().equals("en") ? nameEn : nameFr);
    }

    public String toString(Language language) {
        return (language.name().equalsIgnoreCase("en") ? nameEn : nameFr);
    }
}
