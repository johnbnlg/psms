package com.eunycesoft.psms.data.repository;

import com.eunycesoft.psms.data.ExtendedJpaRepository;
import com.eunycesoft.psms.data.entity.StudentResult;
import com.eunycesoft.psms.data.enums.Classroom;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentResultRepository extends ExtendedJpaRepository<StudentResult, Integer> {
    List<StudentResult> findByTerm1RankLessThanEqual(Integer rank);
    List<StudentResult> findByTerm2RankLessThanEqual(Integer rank);
    List<StudentResult> findByTerm3RankLessThanEqual(Integer rank);
    List<StudentResult> findByYearRankLessThanEqual(Integer yearRank);
    List<StudentResult> findByStudent_Classroom(Classroom classroom);

}

