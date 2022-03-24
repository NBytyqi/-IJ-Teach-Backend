package com.interjoin.teach.dtos.responses;


import com.interjoin.teach.dtos.AvailableHourMinuteDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class AvailableTimesSignupDto {
    private List<AvailableHourMinuteDto> availableTimes = new ArrayList<>();

    public AvailableTimesSignupDto(String timezone) {
        OffsetDateTime startTime = OffsetDateTime.from(OffsetDateTime.now().atZoneSameInstant(ZoneId.of(timezone)))
                .with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
                                                 .withHour(0)
                                                 .withMinute(0)
                                                 .withSecond(0)
                                                 .withNano(0);
        availableTimes.add(AvailableHourMinuteDto.builder()
                        .hourMinuteString(String.format("%s:%s - %s:%s", startTime.getHour(), startTime.getMinute(), startTime.plusHours(1).getHour(), startTime.getMinute()))
                        .dateTime(startTime)
                .build());

        while(startTime.getHour() != 22) {
            startTime = startTime.plusHours(1);
            startTime = startTime.plusMinutes(15);
            availableTimes.add(AvailableHourMinuteDto.builder()
                    .hourMinuteString(String.format("%s:%s - %s:%s", startTime.getHour(), startTime.getMinute(), startTime.plusHours(1).getHour(), startTime.getMinute()))
                    .dateTime(startTime)
                    .build());


        }
        System.out.println(availableTimes);
    }
}
