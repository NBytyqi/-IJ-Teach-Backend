package com.interjoin.teach.controllers;

import com.interjoin.teach.config.exceptions.EmailAlreadyExistsException;
import com.interjoin.teach.dtos.UserDto;
import com.interjoin.teach.dtos.UserSignInRequest;
import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.dtos.requests.OtpVerifyRequest;
import com.interjoin.teach.dtos.responses.AuthResponse;
import com.interjoin.teach.dtos.responses.SignupResponseDto;
import com.interjoin.teach.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService service;

    @PostMapping("/signup/teacher")
    public ResponseEntity<SignupResponseDto> signupTeacher(@Valid @RequestBody UserSignupRequest request) {
        return ResponseEntity.ok(service.createUser(request, "TEACHER"));
    }

    @PostMapping("/profile-pic")
    public void setUserProfilePic(@RequestParam("pic") MultipartFile picture, @RequestParam String userUuid) {
        this.service.addProfilePictureToCurrentUser(picture, userUuid);
    }

    @PostMapping("/cv")
    public void uploadUserCv(@RequestParam("cv") MultipartFile cv, @RequestParam String userUuid) throws IOException {
        this.service.uploadCV(cv, userUuid);
    }

    @PostMapping("/signup/student")
    public ResponseEntity<SignupResponseDto> signupStudent(@Valid @RequestBody UserSignupRequest request) {
        return ResponseEntity.ok(service.createUser(request, "STUDENT"));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signInUser(@Valid @RequestBody UserSignInRequest request) {
        return ResponseEntity.ok(service.signIn(request));
    }

    @GetMapping
    public ResponseEntity<UserDto> getUserDetails() {
        return ResponseEntity.ok(service.getCurrentUserDetailsAsDto());
    }

    @GetMapping("/email")
    public ResponseEntity<Boolean> checkIfUserEmailExists(@RequestParam String email) throws EmailAlreadyExistsException {
        return ResponseEntity.ok(service.emailAlreadyExists(email));
    }

    // Used to verify email on cognito
    @PostMapping("/checkotp")
    public ResponseEntity<Void> checkOtpCode(@RequestBody OtpVerifyRequest request) {
        service.verifyUser(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-otp/{cognitoUsername}")
    public ResponseEntity<Void> resendOtp(@PathVariable String cognitoUsername) {
        service.resendVerificationEmail(cognitoUsername);
        return ResponseEntity.ok().build();
    }

}
