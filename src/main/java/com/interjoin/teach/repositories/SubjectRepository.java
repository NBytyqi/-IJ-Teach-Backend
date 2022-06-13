package com.interjoin.teach.repositories;

import com.interjoin.teach.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Subject findFirstBySubjectName(String sub);

    Optional<Subject> findBySubjectName(String sub);

    List<Subject> findBySubjectNameIn(List<String> subjects);
}
