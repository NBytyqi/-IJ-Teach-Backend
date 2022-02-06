package com.interjoin.teach.dtos;

import com.interjoin.teach.entities.SubjectCurriculum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSignupRequest {

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
    private String dateOfBirth;

    private byte[] profilePicture;

    private String parentEmail;

    private Set<SubjectCurriculum> subCurrList;

}
