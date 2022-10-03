package com.interjoin.teach.services;

import com.interjoin.teach.dtos.RestrictionDto;
import com.interjoin.teach.entities.Restriction;
import com.interjoin.teach.mappers.RestrictionMapper;
import com.interjoin.teach.repositories.RestrictionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestrictionsService {

    private final RestrictionsRepository repository;

    public List<RestrictionDto> saveNewRestrictions(List<RestrictionDto> restrictions) {
        for(RestrictionDto restriction : restrictions) {

            if(Optional.ofNullable(restriction.getProperty()).isPresent() &&
                    Optional.ofNullable(restriction.getValue()).isPresent()) {
                Restriction newRestriction = repository.findFirstByProperty(restriction.getProperty())
                        .orElse(Restriction.builder()
                                .property(restriction.getProperty())
                                .build()
                        );
                newRestriction.setValue(restriction.getValue());
                repository.save(newRestriction);
            }
        }
        return restrictions.stream().filter(res -> res.getProperty() != null && res.getValue() != null).collect(Collectors.toList());
    }

    public List<RestrictionDto> getAllRestrictions() {
        return RestrictionMapper.map(repository.findAll());
    }
}
