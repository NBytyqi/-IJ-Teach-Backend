package com.interjoin.teach.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String location;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private byte[] profilePicture;
    private String parentEmail;
    private String shortBio;
    private String longBio;
}
