package com.interjoin.teach.controllers;

import com.interjoin.teach.config.exceptions.EmailAlreadyExistsException;
import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.dtos.ResetPasswordDTO;
import com.interjoin.teach.dtos.UserDto;
import com.interjoin.teach.dtos.UserSignInRequest;
import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.dtos.requests.AgencySignupRequest;
import com.interjoin.teach.dtos.requests.OtpVerifyRequest;
import com.interjoin.teach.dtos.requests.UpdateProfileRequest;
import com.interjoin.teach.dtos.responses.AuthResponse;
import com.interjoin.teach.dtos.responses.RefreshTokenResponse;
import com.interjoin.teach.dtos.responses.SignupResponseDto;
import com.interjoin.teach.services.SessionService;
import com.interjoin.teach.services.UserService;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService service;
    private final SessionService sessionService;

    @PostMapping("/signup/teacher")
    public ResponseEntity<SignupResponseDto> signupTeacher(@Valid @RequestBody UserSignupRequest request) throws InterjoinException {
        return ResponseEntity.ok(service.createUser(request, "TEACHER"));
    }

    @PostMapping("/profile-pic")
    public void setUserProfilePic(@RequestParam("pic") MultipartFile picture, @RequestParam String userUuid) {
        this.service.addProfilePictureToCurrentUser(picture, userUuid);
    }

    @PutMapping("/forgot")
    public void forgotPassword(@RequestParam("email") String email) throws InterjoinException {
        this.service.forgotPassword(email);
    }

    @PutMapping("/reset")
    public void resetPassword(@Valid @RequestBody  ResetPasswordDTO request) throws InterjoinException {
        this.service.resetPassword(request);
    }

    @PostMapping("/cv")
    public void uploadUserCv(@RequestParam("cv") MultipartFile cv, @RequestParam String userUuid) throws IOException {
        this.service.uploadCV(cv, userUuid);
    }

    @PostMapping("/signup/student")
    public ResponseEntity<SignupResponseDto> signupStudent(@Valid @RequestBody UserSignupRequest request) throws InterjoinException {
        return ResponseEntity.ok(service.createUser(request, "STUDENT"));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signInUser(@Valid @RequestBody UserSignInRequest request) throws InterjoinException {
        return ResponseEntity.ok(service.signIn(request));
    }

    @PostMapping("/signup/agency")
    public ResponseEntity<Void> signupStudent(@Valid @RequestBody AgencySignupRequest request) {
        service.createAgency(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<UserDto> getUserDetails() {
        return ResponseEntity.ok(service.getCurrentUserDetailsAsDto());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Boolean> checkIfUserEmailExists(@PathVariable String email) throws EmailAlreadyExistsException {
        return ResponseEntity.ok(service.emailAlreadyExists(email));
    }

    @PostMapping("/update-profile")
    public ResponseEntity<UserDto> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(service.updateProfile(request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCurrentAccount() throws Exception {
        sessionService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }

    // Used to verify email on cognito
    @PostMapping("/checkotp")
    public ResponseEntity<Void> checkOtpCode(@RequestBody OtpVerifyRequest request) throws InterjoinException {
        service.verifyUser(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-otp/{cognitoUsername}")
    public ResponseEntity<Void> resendOtp(@PathVariable String cognitoUsername) {
        service.resendVerificationEmail(cognitoUsername);
        return ResponseEntity.ok().build();
    }

    private Map<String, Object> getMapFromIoJsonwebtokenClaims(DefaultClaims claims) {
        Map<String, Object> expectedMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        return expectedMap;
    }

}
