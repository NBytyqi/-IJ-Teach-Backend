package com.interjoin.teach.dtos;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionDto {

    private UserDto teacher;
    private String uuid;
    private String studentFullName;
    private OffsetDateTime date;
    private String review;
    private Double reviewScore;
}
