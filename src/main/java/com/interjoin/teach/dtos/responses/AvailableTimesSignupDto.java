package com.interjoin.teach.dtos.responses;


import com.interjoin.teach.dtos.AvailableHourMinuteDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class AvailableTimesSignupDto {
    private List<AvailableHourMinuteDto> availableTimes = new ArrayList<>();

    public AvailableTimesSignupDto(String timezone) {
        Long index = 0L;
        LocalDateTime now = LocalDateTime.now();
        ZoneId zone = ZoneId.of(timezone);
        ZoneOffset zoneOffSet = zone.getRules().getOffset(now);
        OffsetDateTime startTime = OffsetDateTime.
                from(OffsetDateTime.
                                of(2020, 1, 1, 0,0, 0, 0, zoneOffSet));

//                .withYear(2020)
//                .with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
//                .withHour(0)
//                .withMinute(0)
//                .withSecond(0)
//                .withNano(0)



        availableTimes.add(AvailableHourMinuteDto.builder()
                        .hourMinuteString(String.format("%s:%s - %s:%s", startTime.getHour(), startTime.getMinute(), startTime.plusHours(1).getHour(), startTime.getMinute()))
                        .dateTime(startTime)
                        .index(index++)
                .build());

        while(startTime.getHour() != 22) {
            startTime = startTime.plusHours(1);
            startTime = startTime.plusMinutes(15);
            availableTimes.add(AvailableHourMinuteDto.builder()
                    .hourMinuteString(String.format("%s:%s - %s:%s", startTime.getHour(), startTime.getMinute(), startTime.plusHours(1).getHour(), startTime.getMinute()))
                    .dateTime(startTime)
                            .index(index++)
                    .build());


        }
        System.out.println(availableTimes);
    }
}
