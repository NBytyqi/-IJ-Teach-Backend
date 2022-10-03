package com.interjoin.teach.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserSignupRequest {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String timeZone;

    @NotNull
    @Email
    private String email;

    private String location;

    private String phoneNumber;

    @NotNull
    private LocalDate dateOfBirth;

    private String parentEmail;

    private Set<SubjectCurriculumDto> subCurrList;

    @NotNull
    private String shortBio;
    @NotNull
    private String longBio;

    @NotNull
    private String password;

    AvailableTimesSlots availableTimes;

    private List<ExperienceDto> experiences;

    private String qualifications;

    // IN CASE IT IS A TEACHER
    private String agencyReferalCode;

    private BigDecimal pricePerHour;

    private String profilePicture;

}
