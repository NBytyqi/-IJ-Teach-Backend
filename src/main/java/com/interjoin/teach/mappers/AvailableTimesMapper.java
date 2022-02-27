package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.AvailableTimesDto;
import com.interjoin.teach.entities.AvailableTimes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class AvailableTimesMapper {

    public static List<AvailableTimes> map(List<AvailableTimesDto> timesDto) {
        List<AvailableTimes> availableTimes = new ArrayList<>();

        for(AvailableTimesDto dto : timesDto) {
            for(OffsetDateTime dateTime : dto.getAvailableTimes()) {
                availableTimes.add(
                        AvailableTimes.builder()
                                .weekDay(dto.getWeekDay())
//                                .dateTime(dateTime.toLocalDateTime())
                                .build());
            }
        }
        return availableTimes;
    }

//    public static List<AvailableTimes> map(List<AvailableTimesDto> timesDtos) {
//        List<AvailableTimes> avTimes = new ArrayList<>();
//        for(AvailableTimesDto dto : timesDtos) {
//            avTimes.add(map(dto));
//        }
//        return avTimes;
//    }
}
