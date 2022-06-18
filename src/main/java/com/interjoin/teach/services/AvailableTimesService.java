package com.interjoin.teach.services;

import com.interjoin.teach.dtos.AvailableTimesDto;
import com.interjoin.teach.dtos.AvailableTimesStringDto;
import com.interjoin.teach.entities.AvailableTimes;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.repositories.AvailableTimesRepository;
import com.interjoin.teach.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailableTimesService {

    private final AvailableTimesRepository repository;

    @Transactional
    public List<AvailableTimes> save(List<AvailableTimesDto> availableTimes, String timeZone, User teacher) {
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
                        weekDay = DayOfWeek.TUESDAY;
                        break;
                    }
                    case "wednesday": {
                        weekDay = DayOfWeek.WEDNESDAY;
                        break;
                    }
                    case "thursday": {
                        weekDay = DayOfWeek.THURSDAY;
                        break;
                    }
                    case "friday": {
                        weekDay = DayOfWeek.FRIDAY;
                        break;
                    }
                    case "saturday": {
                        weekDay = DayOfWeek.SATURDAY;
                        break;
                    }
                    case "sunday": {
                        weekDay = DayOfWeek.SUNDAY;
                        break;
                    }
                }
                results.add(AvailableTimes.builder()
                        .weekDay(dto.getWeekDay())
                        .dateTime(dateTime.with(TemporalAdjusters.nextOrSame(weekDay)))
                                .teacher(teacher)
                        .build());
            }
        }
        results = repository.saveAll(results);

        return results;
    }

    public void deleteAllByUser(User user) {
        repository.deleteAllByIdInBatch(user.getAvailableTimes().stream().map(AvailableTimes::getId).collect(Collectors.toList()));
    }

    public List<AvailableTimes> findByUser(User user) {
        return user.getAvailableTimes();
    }

    public List<AvailableTimes> findByUserAndWeekDay(Long teacherId, String weekDay) {
        return repository.findByTeacherIdAndWeekDay(teacherId, weekDay);
    }

    public List<AvailableTimesStringDto> findByTeacherAndSpecificDay(Long teacherId, LocalDate date, String studentTimezone) {
        List<AvailableTimes> avTimes = findByUserAndWeekDay(teacherId, date.getDayOfWeek().name().toLowerCase(Locale.ROOT));
        List<AvailableTimesStringDto> avTimesString = DateUtils.map(avTimes, studentTimezone);
        System.out.println(avTimesString);
        return avTimesString;
    }

}
