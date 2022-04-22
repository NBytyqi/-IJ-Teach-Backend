package com.interjoin.teach.services;

import com.interjoin.teach.entities.Experience;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.repositories.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository repository;

    public void save(List<String> experiences, User forUser) {
        experiences.stream().forEach(experience -> {
            repository.save(Experience.builder().experience(experience).user(forUser).build());
        });
    }
}
