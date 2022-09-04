package com.interjoin.teach.controllers;

import com.interjoin.teach.services.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/experience")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    @PutMapping("/{uuid}")
    public ResponseEntity<Void> uploadExperienceImage(@RequestParam("file") MultipartFile file, @PathVariable("uuid") String uuid) throws IOException {
        experienceService.updateExperienceLogo(uuid, file);
        return ResponseEntity.ok().build();
    }
}
