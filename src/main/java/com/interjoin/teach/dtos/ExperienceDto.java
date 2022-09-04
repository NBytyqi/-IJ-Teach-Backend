package com.interjoin.teach.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceDto {

    private Long id;
    private String uuid;
    private String title;
    private String description;
    private String companyName;
    private String awsLogoRef;
    @Column(length = 5000)
    private String awsLogoUrl;
    private String from;
    private String to;
}
