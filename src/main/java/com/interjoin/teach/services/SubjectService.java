package com.interjoin.teach.services;

import com.interjoin.teach.entities.Subject;
import com.interjoin.teach.repositories.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    private SubjectRepository subjectRepository;

    public List<Subject> getSubjectsBySubjectIds(List<Long> subjects) {
        return subjectRepository.findByIdIn(subjects);
    }
}
