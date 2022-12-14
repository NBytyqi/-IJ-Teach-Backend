package com.interjoin.teach.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgencyTeachersList {

    private String agencyName;
    private String shortBio;
    private String location;
    private String awsProfilePictureUrl;
    private List<AgencyTeacher> teachers;

}
