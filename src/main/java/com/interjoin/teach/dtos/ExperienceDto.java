package com.interjoin.teach.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceDto {

    private Long id;
    private String title;
    private String description;
    private String companyName;
    private String base64Logo;
    private byte[] logo;
    private String from;
    private String to;
}
