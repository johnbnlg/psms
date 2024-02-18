package com.eunycesoft.psms.data;

import org.hibernate.Session;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@Transactional(readOnly = true)
public class ExtendedJpaRepositoryImpl<T, ID>
        extends SimpleJpaRepository<T, ID> implements ExtendedJpaRepository<T, ID> {

    private final EntityManager entityManager;

    public ExtendedJpaRepositoryImpl(JpaEntityInformation entityInformation,
                                     EntityManager entityManager) {
        super(entityInformation, entityManager);

        this.entityManager = entityManager;
    }

    @Override
    public Optional<T> findBySimpleNaturalId(String naturalId) {

        Optional<T> entity = entityManager.unwrap(Session.class)
                .bySimpleNaturalId(this.getDomainClass())
                .loadOptional(naturalId);

        return entity;
    }

    //    @Override
//    public Optional<T> findByMultipleNaturalIds(Map<String, Object> naturalIds) {
//
//        NaturalIdLoadAccess<T> loadAccess
//                = entityManager.unwrap(Session.class).byNaturalId(this.getDomainClass());
//        naturalIds.forEach(loadAccess::using);
//
//        return loadAccess.loadOptional();
//    }

}