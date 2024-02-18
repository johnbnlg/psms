package com.eunycesoft.psms.data.entity;

import com.eunycesoft.psms.data.AbstractResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"student"})})
public class StudentResult extends AbstractResult {
    public StudentResult(Student student) {
        super(student);
    }
}