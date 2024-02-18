package com.eunycesoft.psms.data.enums;

import com.eunycesoft.psms.Application;
import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.data.repository.StudentRepository;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Getter
public enum Section {
    FRA("Francophone", "Francophone"),
    ANG("Anglophone", "Anglophone"),
    BIL("Bilingue", "Bilingual");
    private final String nameFr, nameEn;

    Section(String nameFr, String nameEn) {
        this.nameFr = nameFr;
        this.nameEn = nameEn;
    }

    public List<Classroom> getClassrooms() {
        return Arrays.stream(Classroom.values()).filter(cls -> cls.getSection() == this).toList();
    }

    public List<Student> getStudents() {
        var repo = (StudentRepository) Application.repositories.getRepositoryFor(Student.class).get();
        return repo.findByClassroomIn(this.getClassrooms());
    }

    @Override
    public String toString() {
        return (Utils.getSessionLanguage().equals("en") ? nameEn : nameFr);
    }

    public String toString(Locale locale) {
        return (locale.getLanguage().equals("en") ? nameEn : nameFr);
    }
}
