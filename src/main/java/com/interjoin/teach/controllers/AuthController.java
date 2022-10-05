package com.interjoin.teach.controllers;

import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.interjoin.teach.config.exceptions.EmailAlreadyExistsException;
import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.dtos.ResetPasswordDTO;
import com.interjoin.teach.dtos.UserDto;
import com.interjoin.teach.dtos.UserSignInRequest;
import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.dtos.requests.*;
import com.interjoin.teach.dtos.responses.AuthResponse;
import com.interjoin.teach.dtos.responses.RefreshTokenResponse;
import com.interjoin.teach.dtos.responses.SignupResponseDto;
import com.interjoin.teach.entities.AffiliateMarketer;
import com.interjoin.teach.services.AffiliateMarketingService;
import com.interjoin.teach.services.AwsService;
import com.interjoin.teach.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final AwsService awsService;
    private final AffiliateMarketingService afmService;

    @PostMapping("/signup/teacher")
    public ResponseEntity<SignupResponseDto> signupTeacher(@Valid @RequestBody UserSignupRequest request) throws InterjoinException {
        SignUpResult result = awsService.signUpUser(request, "TEACHER");
        AffiliateMarketer affiliateMarketerCode = null;
        String error = null;
        try {
            affiliateMarketerCode = afmService.getAffiliateByReferalCode(request.getAffiliateMarketerCode());
        } catch (InterjoinException e) {
            error = String.format("Referal code %s is invalid!", request.getAffiliateMarketerCode());
        }
        SignupResponseDto response = service.createUser(request, "TEACHER", result.getUserSub(), affiliateMarketerCode);
        response.setReferalCodeNotValid(error);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/profile-pic")
    public void setUserProfilePic(@RequestParam("pic") MultipartFile picture, @RequestParam String userUuid) {
        this.service.addProfilePictureToCurrentUser(picture, userUuid);
    }

    @PutMapping("/forgot/{email}")
    public void forgotPassword(@PathVariable String email) throws InterjoinException {
        this.service.forgotPassword(email);
    }

    @PutMapping("/reset")
    public void resetPassword(@Valid @RequestBody  ResetPasswordDTO request) throws InterjoinException {
        this.service.resetPassword(request);
    }

    @PutMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid LogoutRequest logoutRequest) {
        service.logoutUser(logoutRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> signInWithToken(@RequestBody @Valid RefreshTokenLoginRequest request) {
        return ResponseEntity.ok(service.loginWithRefreshToken(request));
    }

    @PostMapping("/cv")
    public void uploadUserCv(@RequestParam("cv") MultipartFile cv, @RequestParam String userUuid) throws IOException {
        this.service.uploadCV(cv, userUuid);
    }

    @PostMapping("/signup/student")
    public ResponseEntity<SignupResponseDto> signupStudent(@Valid @RequestBody UserSignupRequest request) throws InterjoinException {
        SignUpResult result = awsService.signUpUser(request, "STUDENT");
        return ResponseEntity.ok(service.createUser(request, "STUDENT", result.getUserSub(), null));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signInUser(@Valid @RequestBody UserSignInRequest request) throws InterjoinException {
        AuthResponse authResponse = awsService.signInUser(request);
        authResponse = service.signIn(authResponse, request.getEmail());
        return ResponseEntity.ok(authResponse);
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
        service.emailAlreadyExists(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-profile")
    public ResponseEntity<UserDto> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(service.updateProfile(request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCurrentAccount() throws Exception {
        service.deleteAccount();
        return ResponseEntity.noContent().build();
    }

    // Used to verify email on cognito
    @PostMapping("/checkotp")
    public ResponseEntity<Void> checkOtpCode(@RequestBody OtpVerifyRequest request) throws InterjoinException {
        service.verifyUser(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/checkotp/email")
    public ResponseEntity<Void> checkOtpByEmail(@RequestBody OtpVerifyRequest request) throws InterjoinException {
        service.verifyUserByEmail(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-otp/{cognitoUsername}")
    public ResponseEntity<Void> resendOtp(@PathVariable String cognitoUsername) {
        service.resendVerificationEmail(cognitoUsername);
        return ResponseEntity.ok().build();
    }



}
