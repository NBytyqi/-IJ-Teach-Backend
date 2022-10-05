package com.interjoin.teach.auth;

import com.amazonaws.services.cognitoidp.model.InvalidParameterException;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.amazonaws.services.cognitoidp.model.UserNotConfirmedException;
import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.dtos.UserSignInRequest;
import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.dtos.responses.AuthResponse;
import com.interjoin.teach.dtos.responses.SignupResponseDto;
import com.interjoin.teach.roles.Roles;
import com.interjoin.teach.services.AwsService;
import com.interjoin.teach.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CognitoTests {

    @Autowired
    private UserService userService;

    private AwsService awsService = mock(AwsService.class);

    private UserSignupRequest validSignupRequest;
    private UserSignupRequest inValidSignupRequest;

    private final String upperCaseEmail = "Test4@gmail.com";
    private final String invalidEmail = "invalid @gmail.com";
    private String teacherRole = Roles.TEACHER;
    private String uuid = UUID.randomUUID().toString();
    private String cognitoUsername = UUID.randomUUID().toString();

    @BeforeEach
    public void setup() {
        validSignupRequest = UserSignupRequest.builder()
                .email(upperCaseEmail)
                .firstName("John")
                .lastName("Smith")
                .shortBio("Test short bio")
                .longBio("Test long bio")
                .password("InterJoin2022!")
                .timeZone("Europe/Berlin")
                .pricePerHour(BigDecimal.TEN)
                .build();

        inValidSignupRequest = UserSignupRequest.builder()
                .email(invalidEmail)
                .firstName("John")
                .lastName("Smith")
                .shortBio("Test short bio")
                .longBio("Test long bio")
                .password("InterJoin2022!")
                .timeZone("Europe/Berlin")
                .pricePerHour(BigDecimal.TEN)
                .build();

        signInRequest = UserSignInRequest.builder()
                .email(upperCaseEmail)
                .password("Kosovo2015!")
                .build();
    }

    @Test
    public void testSignupOnValidEmail() throws InterjoinException {
        when(awsService.signUpUser(validSignupRequest, teacherRole)).thenReturn(
                new SignUpResult().withUserSub(cognitoUsername).withUserConfirmed(false)
        );

        SignUpResult result = awsService.signUpUser(validSignupRequest, teacherRole);

        SignupResponseDto response = userService.createUser(validSignupRequest, teacherRole, result.getUserSub(), null);
        assertEquals(response.getFirstName(), "John");
        assertEquals(response.getLastName(), "Smith");
        assertEquals(response.getCognitoUsername(), cognitoUsername);
    }

    @Test
    public void testSignupOnInvalidEmail() throws InterjoinException {
        when(awsService.signUpUser(inValidSignupRequest, teacherRole))
                .thenThrow(new InvalidParameterException("Email is not valid"));

        try {
            awsService.signUpUser(inValidSignupRequest, teacherRole);
        } catch (InvalidParameterException ex) {
            return;
        }
        fail("User signed up correctly");
    }

    private UserSignInRequest signInRequest;

    @Test
    public void testSigninWhenUserNotVerified() throws InterjoinException {
        when(awsService.signInUser(signInRequest)).thenThrow(new UserNotConfirmedException("User is not confirmed"));
        assertThrowsExactly(UserNotConfirmedException.class, () -> awsService.signInUser(signInRequest));
    }

    @AfterEach
    public void deleteUser() {
//        try {
//            this.userService.deleteUserByEmail(upperCaseEmail.toLowerCase());
//        } catch (InterjoinException ex) {
//
//        }
//
//        try {
//            this.userService.deleteUserByEmail(invalidEmail.toLowerCase());
//        } catch (InterjoinException ex) {
//
//        }
    }
}
