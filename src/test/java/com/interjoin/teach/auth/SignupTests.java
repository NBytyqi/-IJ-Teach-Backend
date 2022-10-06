package com.interjoin.teach.auth;

import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.dtos.responses.SignupResponseDto;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.repositories.UserRepository;
import com.interjoin.teach.roles.Roles;
import com.interjoin.teach.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest
class SignupTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private UserSignupRequest userSignupRequest;

    private final String upperCaseEmail = "Test4@gmail.com";

    private String teacherRole = Roles.TEACHER;

    @BeforeEach
    public void setup() {
        userSignupRequest = UserSignupRequest.builder()
                                            .email(upperCaseEmail)
                                            .firstName("John")
                                            .lastName("Smith")
                                            .shortBio("Test short bio")
                                            .longBio("Test long bio")
                                            .password("InterJoin2022!")
                                            .timeZone("Europe/Berlin")
                                            .pricePerHour(BigDecimal.TEN)
                                            .build();
    }

    @AfterEach
    public void deleteUser() throws InterjoinException {
//        this.userService.deleteUserByEmail(upperCaseEmail.toLowerCase());
    }

    @Test
    public void saveUserWithUpperCaseLetterOnEmail() throws InterjoinException {
//        try {
//            SignupResponseDto responseDto = userService.createUser(userSignupRequest, teacherRole);
//            Long userId = responseDto.getUser().getId();
//
//            User user = userService.findById(userId);
//            assertThat(user.getEmail()).isEqualTo(upperCaseEmail.toLowerCase());
//
//        } catch (InterjoinException e) {
//            e.printStackTrace();
//            throw e;
//        }

    }

}