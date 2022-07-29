package com.interjoin.teach.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherDto {

    private Long id;
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
}
