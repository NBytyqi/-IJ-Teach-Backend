package com.interjoin.teach.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
    private String cognitoUsername;
    private LocalDate dateOfBirth;
//    private String profilePicture;
    private String parentEmail;
    private String shortBio;
    private String longBio;
    private Set<SubjectCurriculumResponse> subCurrList;
    private String qualifications;

    private boolean favorite;
    private Long previousSuccessfulSessions;
    private Double rating;
    private List<ExperienceDto> experiences;
    private BigDecimal pricePerHour;
    private String agencyName;
    private boolean verifiedTeacher;

    private Boolean purchasedVerification;

    private BigDecimal listedPrice;
    private String timezone;
    private boolean verifiedEmail;
    private String role;
    private List<String> subjects;
    private List<Long> favoriteTeacherIds;
    private List<ReviewDto> reviews;

    private String awsProfilePictureUrl;

    private String uuid;

    private String awsAgencyLogoUrl;
}
