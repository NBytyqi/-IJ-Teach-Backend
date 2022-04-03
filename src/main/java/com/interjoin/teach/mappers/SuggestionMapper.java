package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.requests.SuggestionDto;
import com.interjoin.teach.entities.Suggestion;

public class SuggestionMapper {

    public static Suggestion map(SuggestionDto suggestion) {
        return Suggestion.builder()
                .type(suggestion.getType())
                .suggestion(suggestion.getSuggestion())
                .build();
    }

    public static SuggestionDto map(Suggestion suggestion) {
        return SuggestionDto.builder()
                .type(suggestion.getType())
                .suggestion(suggestion.getSuggestion())
                .build();
    }
}
