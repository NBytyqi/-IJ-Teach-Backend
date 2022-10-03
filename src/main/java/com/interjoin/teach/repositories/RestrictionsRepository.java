package com.interjoin.teach.repositories;

import com.interjoin.teach.entities.Restriction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestrictionsRepository extends JpaRepository<Restriction, Long> {

    Optional<Restriction> findFirstByProperty(String property);
}
