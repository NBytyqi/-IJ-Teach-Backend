package com.interjoin.teach.repositories;

import com.interjoin.teach.entities.Experience;
import com.interjoin.teach.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findByUser(User user);
    Optional<Experience> findByUuid(String uuid);
}
