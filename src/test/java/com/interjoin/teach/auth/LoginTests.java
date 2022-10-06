package com.interjoin.teach.auth;

import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.repositories.UserRepository;
import com.interjoin.teach.roles.Roles;
import com.interjoin.teach.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
public class LoginTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private UserSignupRequest userSignupRequest;

    private final String upperCaseEmail = "Test3@gmail.com";

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
}
