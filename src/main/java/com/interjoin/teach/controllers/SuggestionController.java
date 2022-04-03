package com.interjoin.teach.controllers;

import com.interjoin.teach.dtos.requests.SuggestionDto;
import com.interjoin.teach.services.SuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/suggest")
@RequiredArgsConstructor
public class SuggestionController {

    private final SuggestionService service;

    @PostMapping()
    public void save(@RequestBody SuggestionDto dto) {
        service.saveSuggestion(dto);
    }
}
