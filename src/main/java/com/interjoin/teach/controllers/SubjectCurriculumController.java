package com.interjoin.teach.controllers;

import com.interjoin.teach.dtos.SubjectCurriculumResponse;
import com.interjoin.teach.services.SubjectCurriculumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.TimeZone;

@RestController
@RequestMapping("/subcurr")
@RequiredArgsConstructor
public class SubjectCurriculumController {

    private final SubjectCurriculumService subCurrService;

    @GetMapping("/all")
    public ResponseEntity<Set<SubjectCurriculumResponse>> getAll(HttpServletRequest request) {
        TimeZone timeZone = RequestContextUtils.getTimeZone(request);
        return ResponseEntity.ok(subCurrService.getAll());
    }


}
