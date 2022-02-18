package com.interjoin.teach.controllers;

import com.interjoin.teach.dtos.SubjectCurriculumResponse;
import com.interjoin.teach.entities.SubjectCurriculum;
import com.interjoin.teach.services.SubjectCurriculumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/subcurr")
@RequiredArgsConstructor
public class SubjectCurriculumController {

    private final SubjectCurriculumService subCurrService;

    @GetMapping("/all")
    public ResponseEntity<Set<SubjectCurriculumResponse>> getAll() {
        return ResponseEntity.ok(subCurrService.getAll());
    }
}
