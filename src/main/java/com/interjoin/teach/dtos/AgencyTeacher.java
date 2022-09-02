package com.interjoin.teach.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class AgencyTeacher {

    private Long id;
    private String firstName;
    private String lastName;
    private Double rating;
    private String location;
    private String shortBio;
    private String longBio;
    private Set<SubjectCurriculumResponse> subCurrList;
    private BigDecimal listedPrice;

    private Long totalHours;
    private BigDecimal totalEarnings;
    private LocalDate dateOfJoiningAgency;
    private String profilePicture;

    private String awsProfilePictureUrl;

}
