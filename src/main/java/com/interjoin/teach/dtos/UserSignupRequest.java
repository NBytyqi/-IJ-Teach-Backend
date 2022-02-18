package com.interjoin.teach.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupRequest {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @Email
    private String email;

    private String location;

    private String phoneNumber;

    @NotNull
    private LocalDate dateOfBirth;

    private byte[] profilePicture;

    private String parentEmail;

    private Set<SubjectCurriculumDto> subCurrList;

    private String shortBio;
    private String longBio;

    @NotNull
    private String password;
}
