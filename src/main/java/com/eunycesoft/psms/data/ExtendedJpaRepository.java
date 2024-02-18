package com.eunycesoft.psms.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface ExtendedJpaRepository<T, ID> extends JpaRepository<T, ID> {

    // use this method when your entity has a single field annotated with @NaturalId
    Optional<T> findBySimpleNaturalId(String naturalId);

    // use this method when your entity has more than one field annotated with @NaturalId
//    Optional<T> findByMultipleNaturalIds(Map<String, Object> naturalIds);

}