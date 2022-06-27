package com.interjoin.teach.repositories;

import com.interjoin.teach.entities.Experience;
import com.interjoin.teach.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findByUser(User user);
}
