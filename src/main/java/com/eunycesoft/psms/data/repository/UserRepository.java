package com.eunycesoft.psms.data.repository;

import com.eunycesoft.psms.data.ExtendedJpaRepository;
import com.eunycesoft.psms.data.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface UserRepository extends ExtendedJpaRepository<User, Integer> {
    User findByUsername(String username);

    @Query(nativeQuery = true, value = "SELECT MAX(CONVERT(SUBSTR(registration_number,8), UNSIGNED)) FROM user WHERE registration_number LIKE ?1%")
    Integer getLastRegistrationNumber(String start);

}