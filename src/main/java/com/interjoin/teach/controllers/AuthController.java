package com.interjoin.teach.controllers;

import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/signup/teacher")
    public ResponseEntity<Void> signupTeacher(@RequestBody UserSignupRequest request) {
        service.signUpUser(request, "TEACHER");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup/student")
    public ResponseEntity<Void> signupStudent(@RequestBody UserSignupRequest request) {
        service.signUpUser(request, "STUDENT");
        return ResponseEntity.ok().build();
    }
}
