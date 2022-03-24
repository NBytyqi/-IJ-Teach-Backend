package com.interjoin.teach.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectCurriculumDto {

    private String curriculumName;
    private List<String> subjectNames;
}