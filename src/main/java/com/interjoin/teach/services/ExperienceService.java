package com.interjoin.teach.services;

import com.interjoin.teach.dtos.ExperienceDto;
import com.interjoin.teach.entities.Experience;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.mappers.ExperienceMapper;
import com.interjoin.teach.repositories.ExperienceRepository;
import lombok.RequiredArgsConstructor;
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
    private final AwsService awsService;

    public List<Experience> save(List<ExperienceDto> experiences, User forUser) {
//        experiences.stream().forEach(experience -> {
        return repository.saveAll(ExperienceMapper.map(experiences, forUser));
//        });
    }

    @Transactional
    public void deleteForUser( User forUser) {
        repository.deleteAllInBatch(repository.findByUser(forUser));
    }

    public void updateExperienceLogo(String uuid, MultipartFile file) throws IOException {
        Experience experience = repository.findByUuid(uuid).orElseThrow(EntityNotFoundException::new);
        final String FILE_REF = String.format("%s/%d/%s", "Experiences", experience.getId(), file.getOriginalFilename());
        awsService.uploadFile(FILE_REF, file);
        experience.setAwsLogoUrl(awsService.generatePresignedUrl(FILE_REF));
        experience.setAwsLogoRef(FILE_REF);
        repository.save(experience);
    }

    public List<Experience> findAllWithLogos() {
        return repository.findByAwsLogoRefIsNotNull();
    }
}
