package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.CurriculumDto;
import com.interjoin.teach.entities.Curriculum;

public class CurriculumMapper {

    public static CurriculumDto map(Curriculum curriculum) {
        return CurriculumDto.builder()
                .id(curriculum.getId())
                .curriculumName(curriculum.getCurriculumName())
                .subjects(SubjectMapper.map(curriculum.getSubjects()))
                .build();
    }
}
