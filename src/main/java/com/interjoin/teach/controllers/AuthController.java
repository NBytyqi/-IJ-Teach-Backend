package com.interjoin.teach.controllers;

import com.interjoin.teach.dtos.UserDto;
import com.interjoin.teach.dtos.UserSignInRequest;
import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.dtos.responses.AuthResponse;
import com.interjoin.teach.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService service;

    @PostMapping("/signup/teacher")
    public ResponseEntity<Void> signupTeacher(@Valid @RequestBody UserSignupRequest request) {
        service.createUser(request, "TEACHER");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup/student")
    public ResponseEntity<Void> signupStudent(@Valid @RequestBody UserSignupRequest request) {
        service.createUser(request, "STUDENT");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signInUser(@Valid @RequestBody UserSignInRequest request) {
        return ResponseEntity.ok(service.signIn(request));
    }

    @GetMapping
    public ResponseEntity<UserDto> getUserDetails() {
        return ResponseEntity.ok(service.getCurrentUserDetailsAsDto());
    }
}
