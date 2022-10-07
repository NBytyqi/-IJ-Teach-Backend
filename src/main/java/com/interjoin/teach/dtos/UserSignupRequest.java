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

    @NotNull(message = "First name cannot be empty")
    private String firstName;

    @NotNull(message = "Last name cannot be empty")
    private String lastName;

    @NotNull(message = "Timezone name cannot be empty")
    private String timeZone;

    @NotNull(message = "Email cannot be empty")
    @Email
    private String email;

    private String location;

    private String phoneNumber;

    @NotNull(message = "Date of birth cannot be empty")
    private LocalDate dateOfBirth;

    private String parentEmail;

    private Set<SubjectCurriculumDto> subCurrList;

    @NotNull(message = "Short bio cannot be empty")
    private String shortBio;
    @NotNull(message = "Long bio cannot be empty")
    private String longBio;

    @NotNull(message = "Password cannot be empty")
    private String password;

    AvailableTimesSlots availableTimes;

    private List<ExperienceDto> experiences;

    private String qualifications;

    // IN CASE IT IS A TEACHER
    private String agencyReferalCode;

    private BigDecimal pricePerHour;

    private String profilePicture;

    private String affiliateMarketerCode;

}
