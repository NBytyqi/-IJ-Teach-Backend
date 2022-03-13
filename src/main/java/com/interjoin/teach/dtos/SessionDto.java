package com.interjoin.teach.dtos;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionDto {

    private String teacherFullName;
    private String studentFullName;
    private OffsetDateTime date;
}
