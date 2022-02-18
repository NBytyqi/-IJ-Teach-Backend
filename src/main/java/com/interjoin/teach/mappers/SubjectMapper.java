package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.SubjectDto;
import com.interjoin.teach.entities.Subject;

import java.util.HashSet;
import java.util.Set;

public class SubjectMapper {

    public static SubjectDto map(Subject subject) {
        return SubjectDto.builder()
                .id(subject.getId())
                .subjectName(subject.getSubjectName())
                .build();
    }

    public static Subject map(SubjectDto subject) {
        return Subject.builder()
                .id(subject.getId())
                .subjectName(subject.getSubjectName())
                .build();
    }

    public static Set<SubjectDto> map(Set<Subject> subjects) {
        Set<SubjectDto> subs = new HashSet<>();
        for(Subject s : subjects) {
            subs.add(map(s));
        }
        return subs;
    }
}
