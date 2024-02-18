package com.eunycesoft.psms.data.entity;

import com.eunycesoft.psms.Utils;
import com.eunycesoft.psms.data.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Locale;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Appreciation extends AbstractEntity {

    @NotNull
    @Column(unique = true)
    private double min, max;
    @NotNull
    @Column(unique = true)
    private String codeFr, codeEn, nameFr, nameEn;


    @Override
    public String toString() {
        return (Utils.getSessionLanguage().equals("en") ? codeEn : codeFr);
    }

    public String toString(Locale locale) {
        return (locale.getLanguage().equals("en") ? codeEn : codeFr);
    }
}
