package com.interjoin.teach.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherDto {

    private Long id;
    private String uuid;
    private String firstName;
    private String lastName;
    private String profilePicture;


    private String agencyName;
    private String qualifications;
    private BigDecimal listedPrice;
    private String longBio;
    private String shortBio;
    private String location;
    private Double rating;
    private Boolean verifiedTeacher;

    private String subjectsStr;
    private String curriculumsStr;

    private String awsProfilePictureUrl;
}
