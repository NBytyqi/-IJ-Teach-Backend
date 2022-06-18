package com.interjoin.teach.controllers;

import com.interjoin.teach.dtos.AvailableHourMinuteDto;
import com.interjoin.teach.dtos.AvailableTimesDto;
import com.interjoin.teach.dtos.AvailableTimesStringDto;
import com.interjoin.teach.dtos.responses.AvailableTimesSignupDto;
import com.interjoin.teach.services.AvailableTimesService;
import com.interjoin.teach.services.SessionService;
import com.interjoin.teach.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/times")
@RequiredArgsConstructor
public class TimesController {

    private final AvailableTimesService service;
    private final UserService userService;
    private final SessionService sessionService;

    @GetMapping("/signup")
    public AvailableTimesSignupDto getAvailableTimes(@RequestParam String timezone) {
        return new AvailableTimesSignupDto(timezone);
    }

//    @GetMapping("/teacher/{teacherId}")
//    public ResponseEntity<List<AvailableTimesStringDto>> getTeacherAvailableTimes(@PathVariable Long teacherId) {
//        return ResponseEntity.ok(userService.getAvailableTimesForTeacher(teacherId));
//    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<AvailableTimesDto>> getTeacherAvailableTimes(@PathVariable Long teacherId) {
        return ResponseEntity.ok(userService.getAvailableTimesForTeacher(teacherId));
    }

    @GetMapping("/specific-date/{teacherId}")
    public ResponseEntity<List<AvailableHourMinuteDto>> getTeacherAvailableTimesForASpecificDate(@PathVariable Long teacherId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(sessionService.availableTimesPerDay(teacherId, date));
    }

}
