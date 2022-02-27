package com.interjoin.teach.services;

import com.interjoin.teach.dtos.AvailableTimesDto;
import com.interjoin.teach.entities.AvailableTimes;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.repositories.AvailableTimesRepository;
import com.interjoin.teach.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailableTimesService {

    private final AvailableTimesRepository repository;

    public List<AvailableTimes> save(List<AvailableTimesDto> availableTimes, String timeZone) {
        List<AvailableTimes> results = new ArrayList<>();

        for(AvailableTimesDto dto: availableTimes) {
            for(OffsetDateTime dateTime : dto.getAvailableTimes()) {
                results.add(AvailableTimes.builder()
                        .weekDay(dto.getWeekDay())
                        .dateTime(DateUtils.map(dateTime, timeZone))
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
