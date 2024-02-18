package com.eunycesoft.psms.data.repository;

import com.eunycesoft.psms.data.ExtendedJpaRepository;
import com.eunycesoft.psms.data.entity.Appreciation;
import org.springframework.stereotype.Repository;

@Repository
public interface AppreciationRepository extends ExtendedJpaRepository<Appreciation, Integer> {

}

