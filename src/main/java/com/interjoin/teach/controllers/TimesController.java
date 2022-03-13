package com.interjoin.teach.controllers;

import com.interjoin.teach.dtos.AvailableTimesStringDto;
import com.interjoin.teach.dtos.responses.AvailableTimesSignupDto;
import com.interjoin.teach.services.AvailableTimesService;
import com.interjoin.teach.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/times")
@RequiredArgsConstructor
public class TimesController {

    private final AvailableTimesService service;
    private final UserService userService;

    @GetMapping("/signup")
    public AvailableTimesSignupDto getAvailableTimes(@RequestParam String timezone) {
        return new AvailableTimesSignupDto(timezone);
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<AvailableTimesStringDto>> getTeacherAvailableTimes(@PathVariable Long teacherId) {
        return ResponseEntity.ok(userService.getAvailableTimesForTeacher(teacherId));
    }
}
