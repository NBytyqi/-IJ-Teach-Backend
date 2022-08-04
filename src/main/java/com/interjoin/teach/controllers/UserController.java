package com.interjoin.teach.controllers;

import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.dtos.TeacherInfo;
import com.interjoin.teach.dtos.UserDto;
import com.interjoin.teach.dtos.requests.TeacherFilterRequest;
import com.interjoin.teach.services.UserService;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.jcajce.provider.symmetric.TEA;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserAsDto());
    }

    @PostMapping(path = "/interjoin-verification")
    public ResponseEntity<?> purschaseVerification(@RequestParam(name = "process") String process) {
        return ResponseEntity.ok(userService.purchaseVerification(process));
    }

    @PostMapping(path = "/teachers")
    public ResponseEntity<List<TeacherInfo>> getFilteredTeachers(@RequestBody TeacherFilterRequest filterRequest) {
        return ResponseEntity.ok(userService.getFilteredTeachers(filterRequest));
    }

    @PutMapping(path = "/favorite/{teacherId}")
    public ResponseEntity<Void> favoriteTeacherById(@PathVariable("teacherId") Long teacherId, @RequestParam boolean favorite) {
        userService.favoriteTeacher(teacherId, favorite);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<String>> getCurriculumsOfSubjectAndTeacher(@RequestParam Long teacherId, @RequestParam String subject) throws InterjoinException {
        return ResponseEntity.ok(userService.getCurriculumsOfSubjectAndTeacher(teacherId, subject));
    }


}
