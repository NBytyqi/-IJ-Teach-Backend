package com.interjoin.teach.controllers;

import com.interjoin.teach.dtos.ActivityLogDto;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.mappers.ActivityLogMapper;
import com.interjoin.teach.services.ActivityLogsService;
import com.interjoin.teach.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class ActivityLogsController {

    private final UserService userService;
    private final ActivityLogsService service;

    @GetMapping
    public ResponseEntity<List<ActivityLogDto>> getLogs() {
        User agency = userService.getCurrentUserDetails();
        return ResponseEntity.ok(ActivityLogMapper.map(service.findByAgency(agency)));
    }
}
