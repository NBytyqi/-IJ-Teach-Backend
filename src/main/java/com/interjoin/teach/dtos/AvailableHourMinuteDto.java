package com.interjoin.teach.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
public class AvailableHourMinuteDto {
    private String hourMinuteString;
    private OffsetDateTime dateTime;

    private LocalDate dateOfSession;
}
