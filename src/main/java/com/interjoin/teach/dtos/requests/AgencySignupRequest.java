package com.interjoin.teach.dtos.requests;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
public class AgencySignupRequest {
    @NotNull
    private String agencyName;
    @NotNull
    @Email
    private String contactEmail;
    private String location;
    private Integer numberOfTeachers;
    private String additionalComments;
}
