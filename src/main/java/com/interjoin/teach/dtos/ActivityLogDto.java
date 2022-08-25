package com.interjoin.teach.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ActivityLogDto {

    private String log;
    private LocalDateTime createdDate;

}
