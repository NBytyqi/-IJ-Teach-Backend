package com.interjoin.teach.repositories;

import com.interjoin.teach.entities.AffiliateMarketer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AffiliateMarketerRepository extends JpaRepository<AffiliateMarketer, Long> {

    Optional<AffiliateMarketer> findByEmail(String email);
    Optional<AffiliateMarketer> findByReferalCode(String referalCode);

}
