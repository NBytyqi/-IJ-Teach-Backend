package com.interjoin.teach.services;

import com.interjoin.teach.dtos.SubjectCurriculumResponse;
import com.interjoin.teach.mappers.SubjectCurriculumMapper;
import com.interjoin.teach.repositories.SubjectCurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubjectCurriculumService {

    private final SubjectCurriculumRepository repository;
    private final CurriculumService curriculumService;

    public Set<SubjectCurriculumResponse> getAll() {
        return SubjectCurriculumMapper.map(curriculumService.getAll());
    }

    public List<Long> getTeachersForSubjects(List<Long> subjectIds) {
        return repository.getTeachersForSubjects(subjectIds);
    }
}
