package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.AvailableTimesDto;
import com.interjoin.teach.entities.AvailableTimes;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static List<AvailableTimesDto> mapThem(List<AvailableTimes> times) {
        List<AvailableTimesDto> availableTimes = new ArrayList<>();

        Map<String, List<AvailableTimes>> mapped = times.stream().collect(Collectors.groupingBy(AvailableTimes::getWeekDay));

        for(String key : mapped.keySet()) {
            availableTimes.add(AvailableTimesDto.builder()
                            .weekDay(transformDayName(key))
                            .availableTimes(mapped.get(key).stream().map(AvailableTimes::getDateTime).collect(Collectors.toList()))

                    .build());
        }

        return availableTimes;
    }

    public static String transformDayName(String day) {
        String finalDay = "";
        switch(day) {
            case "monday": {
                finalDay = "Mon";
                break;
            }
            case "tuesday": {
                finalDay = "Tue";
                break;
            }
            case "wednesday": {
                finalDay = "Wed";
                break;
            }
            case "thursday": {
                finalDay = "Thu";
                break;
            }
            case "friday": {
                finalDay = "Fri";
                break;
            }
            case "saturday": {
                finalDay = "Sat";
                break;
            }
            case "sunday": {
                finalDay = "Sun";
                break;
            }
        }
        return finalDay;
    }

//    public static List<AvailableTimes> map(List<AvailableTimesDto> timesDtos) {
//        List<AvailableTimes> avTimes = new ArrayList<>();
//        for(AvailableTimesDto dto : timesDtos) {
//            avTimes.add(map(dto));
//        }
//        return avTimes;
//    }
}
