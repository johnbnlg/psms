package com.eunycesoft.psms.data;

import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.data.enums.Language;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
@Setter
@Getter
@NoArgsConstructor
public class AbstractResultWithTotal extends AbstractResult {

    @NotNull
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval1total;
    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval1over;

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval2total;
    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval2over;

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval3total;
    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval3over;

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval4total;
    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval4over;

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval5total;
    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval5over;

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval6total;
    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval6over;

    public AbstractResultWithTotal(Student student, Language language) {
        super(student);
        this.language = language;
    }
}
