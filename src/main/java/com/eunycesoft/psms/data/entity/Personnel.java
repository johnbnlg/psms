package com.eunycesoft.psms.data.entity;

import com.eunycesoft.psms.data.enums.Gender;
import com.eunycesoft.psms.data.enums.Language;
import com.eunycesoft.psms.data.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Personnel extends User {
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Role> otherRoles = new ArrayList<>();
    @Column(unique = true)
    private Integer phone1, phone2;
    @Email
    @Column(unique = true)
    private String email;

    public Personnel(String registrationNumber, String name, String surname, Language language, Gender gender, Role mainRole) {
        super(registrationNumber, name, surname, language, gender, mainRole);
    }

    @Override
    public String toString() {
        return getCivilName();
    }

    @PrePersist
    public void prePersist() {
        super.prePersist();
    }
}
