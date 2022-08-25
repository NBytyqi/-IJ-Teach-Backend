package com.interjoin.teach.services;

import com.interjoin.teach.dtos.ExperienceDto;
import com.interjoin.teach.entities.Experience;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.mappers.ExperienceMapper;
import com.interjoin.teach.repositories.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository repository;

    public void save(List<ExperienceDto> experiences, User forUser) {
//        experiences.stream().forEach(experience -> {
            repository.saveAll(ExperienceMapper.map(experiences, forUser));
//        });
    }

    @Transactional
    public void deleteForUser( User forUser) {
        repository.deleteAllInBatch(repository.findByUser(forUser));
    }

    public void updateExperienceLogo(Long experienceId, MultipartFile file) throws IOException {
//        Experience experience = repository.findById(experienceId).orElseThrow(EntityNotFoundException::new);
//        experience.setLogo(IOUtils.toByteArray(file.getInputStream()));
//        repository.save(experience);
    }
}
