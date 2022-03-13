package com.interjoin.teach.dtos.requests;

import com.interjoin.teach.dtos.AvailableHourMinuteDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
public class BookSessionRequest {

    @NotNull
    private AvailableHourMinuteDto date;
    @NotNull
    private Long teacherId;
}
