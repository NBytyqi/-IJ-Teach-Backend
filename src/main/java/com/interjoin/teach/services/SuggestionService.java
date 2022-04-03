package com.interjoin.teach.services;

import com.interjoin.teach.dtos.requests.SuggestionDto;
import com.interjoin.teach.mappers.SuggestionMapper;
import com.interjoin.teach.repositories.SuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuggestionService {

    private final SuggestionRepository repository;

    public void saveSuggestion(SuggestionDto dto) {
        repository.save(SuggestionMapper.map(dto));
    }

}
