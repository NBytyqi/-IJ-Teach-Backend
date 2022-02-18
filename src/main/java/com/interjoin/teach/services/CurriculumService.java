package com.interjoin.teach.services;

import com.interjoin.teach.entities.Curriculum;
import com.interjoin.teach.repositories.CurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurriculumService {

    private final CurriculumRepository repository;

    public List<Curriculum> getAll() {
        return repository.findAll();
    }
}
