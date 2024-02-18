package com.eunycesoft.psms.data.entity;

import com.eunycesoft.psms.data.AbstractEntity;
import com.eunycesoft.psms.data.enums.Gender;
import com.eunycesoft.psms.data.enums.Language;
import com.eunycesoft.psms.data.enums.Role;
import com.eunycesoft.psms.data.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.text.WordUtils;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

import static com.eunycesoft.psms.Application.repositories;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends AbstractEntity implements Serializable {
    @NotNull
    @Column(unique = true, length = 11)
    @NaturalId
    private String registrationNumber = "MOR";
    @NotNull
    private String name;

    private String surname;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Language language;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String neighborhood;
    @Column(unique = true)
    private String username;
    private String password;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Role mainRole;

    public User(String registrationNumber, String name, String surname, Language language, Gender gender, LocalDate dateOfBirth, String placeOfBirth, Role mainRole) {
        this(registrationNumber, name, surname, language, gender, mainRole);
        this.dateOfBirth = dateOfBirth;
        this.placeOfBirth = placeOfBirth;
    }

    public User(String registrationNumber, String name, String surname, Language language, Gender gender, Role mainRole) {
        this.registrationNumber = registrationNumber;
        this.name = name;
        this.surname = surname;
        this.language = language;
        this.gender = gender;
        this.mainRole = mainRole;
    }

    public String getFullName() {
        return surname != null ? (name + " " + surname) : name;
    }

    public String getCivilName() {
        return (gender.equals(Gender.F) ? "Mme " : "M. ") + name.split("\\s+")[0];
    }

    @Override
    public String toString() {
        return username;
    }

    public void prePersist() {
        name = name.toUpperCase();
        surname = WordUtils.capitalizeFully(surname);
        if (registrationNumber.equals("MOR")) {
            var userRepo = (UserRepository) (repositories.getRepositoryFor(User.class).get());
            if (mainRole.equals(Role.STUDENT)) {
                registrationNumber = String.format("MORS%d%s%03d", LocalDate.now().getYear() - 2000, gender.name(),
                        userRepo.getLastRegistrationNumber("MORS") + 1);
            } else {
                registrationNumber = String.format("MORP%d%s%03d", LocalDate.now().getYear() - 2000, gender.name(),
                        userRepo.getLastRegistrationNumber("MORP") + 1);
            }
        }
        if (username == null) {
            username = registrationNumber;
            password = new BCryptPasswordEncoder().encode(registrationNumber);
        }
    }
}
