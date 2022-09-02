package com.interjoin.teach.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.interjoin.teach.dtos.ExperienceDto;
import com.interjoin.teach.dtos.SubjectCurriculumDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@JsonIgnoreProperties
public class UpdateProfileRequest {
    private String password;
    private String location;
    private String phoneNumber;

    private BigDecimal pricePerHour;
    private String shortBio;
    private String longBio;

    private Set<SubjectCurriculumDto> subCurrList;
    private List<ExperienceDto> experiences;
    private String timezone;
    private List<Long> favoriteTeacherIds;

//    private String profilePicture;


}
