package com.interjoin.teach.mappers;


import com.interjoin.teach.dtos.ActivityLogDto;
import com.interjoin.teach.entities.ActivityLogs;

import java.util.ArrayList;
import java.util.List;

public class ActivityLogMapper {

    public static ActivityLogDto map(ActivityLogs log) {
        return ActivityLogDto.builder()
                .log(log.getLog())
                .createdDate(log.getCreatedDate())
                .build();
    }

    public static List<ActivityLogDto> map(List<ActivityLogs> logs) {
        List<ActivityLogDto> logsDto = new ArrayList<>();
        for(ActivityLogs ac : logs) {
            logsDto.add(map(ac));
        }
        return logsDto;
    }
}
