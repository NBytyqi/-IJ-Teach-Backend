package com.interjoin.teach.dtos;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionDto {

    private UserDto student;
    private String uuid;
    private String studentFullName;
    private OffsetDateTime date;
    private String review;
    private Double reviewScore;
    private String subject;
    private String curriculum;
    private String comment;
}
