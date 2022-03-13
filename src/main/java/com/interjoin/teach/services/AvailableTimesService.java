package com.interjoin.teach.services;

import com.interjoin.teach.dtos.AvailableTimesDto;
import com.interjoin.teach.entities.AvailableTimes;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.repositories.AvailableTimesRepository;
import com.interjoin.teach.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailableTimesService {

    private final AvailableTimesRepository repository;

    public List<AvailableTimes> save(List<AvailableTimesDto> availableTimes, String timeZone) {
        List<AvailableTimes> results = new ArrayList<>();
        DayOfWeek weekDay = DayOfWeek.MONDAY;
        for(AvailableTimesDto dto: availableTimes) {
            for(OffsetDateTime dateTime : dto.getAvailableTimes()) {
                switch(dto.getWeekDay()) {
                    case "monday": {
                        weekDay = DayOfWeek.MONDAY;
                        break;
                    }
                    case "tuesday": {
                        weekDay = DayOfWeek.MONDAY;
                        break;
                    }
                    case "wednesday": {
                        weekDay = DayOfWeek.MONDAY;
                        break;
                    }
                    case "thursday": {
                        weekDay = DayOfWeek.MONDAY;
                        break;
                    }
                    case "friday": {
                        weekDay = DayOfWeek.MONDAY;
                        break;
                    }
                    case "saturday": {
                        weekDay = DayOfWeek.MONDAY;
                        break;
                    }
                    case "sunday": {
                        weekDay = DayOfWeek.MONDAY;
                        break;
                    }
                }
                results.add(AvailableTimes.builder()
                        .weekDay(dto.getWeekDay())
                        .dateTime(dateTime.with(TemporalAdjusters.nextOrSame(weekDay)))
                        .build());
            }
        }
        results = repository.saveAll(results);

        return results;
    }

    public List<AvailableTimes> findByUser(User user) {
        return user.getAvailableTimes();
    }
}
