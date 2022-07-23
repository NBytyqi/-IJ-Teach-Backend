package com.interjoin.teach.repositories;

import com.interjoin.teach.entities.SubjectCurriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectCurriculumRepository extends JpaRepository<SubjectCurriculum, Long> {

    SubjectCurriculum findFirstByCurriculumCurriculumNameAndSubjectSubjectName(String currName, String subName);

    @Query(value = "SELECT ucs.user_id from user_curriculum_subject ucs where ucs.subject_id in (:subjects)",
    nativeQuery = true)
    List<Long> getTeachersForSubjects(@Param("subjects") List<Long> subjects);
}
