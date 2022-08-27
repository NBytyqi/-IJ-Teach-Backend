package com.interjoin.teach.utils;

import com.interjoin.teach.dtos.AvailableHourMinuteDto;
import com.interjoin.teach.dtos.AvailableTimesDto;
import com.interjoin.teach.dtos.AvailableTimesStringDto;
import com.interjoin.teach.entities.AvailableTimes;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DateUtils {


    public static List<AvailableTimesStringDto> map(List<AvailableTimes> times, String timeZone) {
        List<AvailableTimesStringDto> availableTimesStringDtos = new ArrayList<>();

        Map<String, List<AvailableTimes>> listMap = times.stream().collect(Collectors.groupingBy(AvailableTimes::getWeekDay));

        for(String key : listMap.keySet()) {
            availableTimesStringDtos.add(map(listMap.get(key).stream().map(AvailableTimes::getDateTime).collect(Collectors.toList()), timeZone, key));
        }
        return availableTimesStringDtos;
    }

    public static AvailableTimesStringDto map(List<OffsetDateTime> fromTimes, String timeZone, String weekDay) {
        AvailableTimesStringDto hourMinutes = AvailableTimesStringDto.builder().build();
        List<AvailableHourMinuteDto> available = new ArrayList<>();

        for(OffsetDateTime offsetDateTime : fromTimes) {
            LocalDateTime now = LocalDateTime.now();
            ZoneId zone = ZoneId.of(timeZone);
            ZoneOffset zoneOffSet = zone.getRules().getOffset(now);
            // converted at student timezone
            OffsetDateTime converted = OffsetDateTime.from(offsetDateTime.atZoneSameInstant(zoneOffSet));

            available.add(AvailableHourMinuteDto.builder()
                            .dateTime(converted)
                            .hourMinuteString(String.format("%s:%s - %s:%s", converted.getHour(), converted.getMinute(), converted.plusHours(1).getHour(), converted.getMinute()))
                    .build());
        }
        hourMinutes.setWeekDay(weekDay);
        hourMinutes.setAvailableHourMinute(available);
        return hourMinutes;
    }

    public static OffsetDateTime map(OffsetDateTime time, String timeZone) {
        return OffsetDateTime.from(time.atZoneSameInstant(ZoneId.of(timeZone)));
    }

    public static OffsetDateTime map(OffsetDateTime time, String timeZone, boolean test) {
        LocalDateTime now = LocalDateTime.now();
        ZoneId zone = ZoneId.of(timeZone);
        ZoneOffset zoneOffSet = zone.getRules().getOffset(now);
        return OffsetDateTime.from(time.atZoneSameInstant(zoneOffSet));
    }

    public static List<OffsetDateTime> mapMultipleTimes(List<OffsetDateTime> times, String timeZone) {
        List<OffsetDateTime> dates = new ArrayList<>();
        for(OffsetDateTime time : times) {
            dates.add(map(time, timeZone));
        }
        return dates;
    }

    public static void main(String[] arg) {
        List<OffsetDateTime> dates = Arrays.asList(OffsetDateTime.now(), OffsetDateTime.now().plusHours(1));
//        OffsetDateTime date = OffsetDateTime.now();
//        System.out.println(date.getHour() + ":" + date.getMinute());
        AvailableTimesStringDto result = map(dates, "Africa/Djibouti", "monday");
        System.out.println(result);
    }
}
