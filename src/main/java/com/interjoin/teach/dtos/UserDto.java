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
    private LocalDate dateOfBirth;
    private byte[] profilePicture;
    private String parentEmail;
    private String shortBio;
    private String longBio;
    private Set<SubjectCurriculumResponse> subCurrList;
    private String qualifications;

    private boolean favorite;
    private Long previousSuccessfulSessions;
    private Double rating;
    private List<String> experiences;
    private BigDecimal pricePerHour;
    private String agencyName;
    private boolean verifiedTeacher;
    private BigDecimal listedPrice;
}
