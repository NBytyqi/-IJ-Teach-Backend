package com.interjoin.teach.repositories;

import com.interjoin.teach.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    Optional<User> findByCognitoUsername(String username);
    Optional<User> findByUuid(String uuid);
    Optional<User> findByEmail(String email);

    // GET AGENCY USERS
    Page<User> findByAgencyAndAgencyName(boolean isAgency, String agencyName, Pageable pageable);

    Optional<User> findFirstByAgencyCode(String agencyCode);
}
