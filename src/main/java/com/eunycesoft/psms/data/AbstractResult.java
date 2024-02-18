package com.eunycesoft.psms.data;

import com.eunycesoft.psms.data.entity.Student;
import com.eunycesoft.psms.data.enums.Classroom;
import com.eunycesoft.psms.data.enums.Language;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@MappedSuperclass
@Setter
@Getter
@NoArgsConstructor
public class AbstractResult extends AbstractEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "student")
    private Student student;

    private String updateBuffer;

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval1Average;
    private String eval1Appreciation;
    private Integer eval1Rank;

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval2Average;
    private String eval2Appreciation;
    private Integer eval2Rank;

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double term1Average;
    private String term1Appreciation;
    private Integer term1Rank;
    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval3Average;
    private String eval3Appreciation;
    private Integer eval3Rank;

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval4Average;
    private String eval4Appreciation;
    private Integer eval4Rank;

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double term2Average;
    private String term2Appreciation;
    private Integer term2Rank;
    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval5Average;
    private String eval5Appreciation;
    private Integer eval5Rank;

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double eval6Average;
    private String eval6Appreciation;
    private Integer eval6Rank;

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double term3Average;
    private String term3Appreciation;
    private Integer term3Rank;

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double yearAverage;
    private String yearAppreciation;
    private Integer yearRank;

    public AbstractResult(Student student) {
        this.student = student;
    }
}
