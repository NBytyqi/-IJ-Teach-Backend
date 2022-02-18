package com.interjoin.teach.repositories;

import com.interjoin.teach.entities.SubjectCurriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectCurriculumRepository extends JpaRepository<SubjectCurriculum, Long> {

    SubjectCurriculum findFirstByCurriculumCurriculumNameAndSubjectSubjectName(String currName, String subName);
}
