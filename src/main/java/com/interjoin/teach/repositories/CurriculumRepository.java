package com.interjoin.teach.repositories;

import com.interjoin.teach.entities.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

    Curriculum findFirstByCurriculumName(String curr);
}
