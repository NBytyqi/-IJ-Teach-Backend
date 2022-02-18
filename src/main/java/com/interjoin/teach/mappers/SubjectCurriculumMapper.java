package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.SubjectCurriculumResponse;
import com.interjoin.teach.entities.Curriculum;
import com.interjoin.teach.entities.Subject;
import com.interjoin.teach.entities.SubjectCurriculum;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SubjectCurriculumMapper {

    public static SubjectCurriculumResponse map(Curriculum curriculum) {
        return SubjectCurriculumResponse.builder()
                .curriculum(curriculum.getCurriculumName())
                .subjects(curriculum.getSubjects().stream().map(Subject::getSubjectName).collect(Collectors.toSet()))
                .build();
    }

    public static Set<SubjectCurriculumResponse> map(List<Curriculum> curriculums) {
        Set<SubjectCurriculumResponse> results = new HashSet<>();
        for(Curriculum c : curriculums) {
            results.add(map(c));
        }
        return results;
    }

    public static Set<SubjectCurriculumResponse> map(Set<SubjectCurriculum> currs) {
        Set<SubjectCurriculumResponse> results = new HashSet<>();

        for(SubjectCurriculum curr : currs) {
//            results.add(map(curr));
        }
        return results;
     }
}
