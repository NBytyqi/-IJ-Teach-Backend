package com.interjoin.teach.services;

import com.interjoin.teach.entities.ActivityLogs;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.repositories.ActivityLogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogsService {

    private final ActivityLogsRepository repository;

    private List<ActivityLogs> findByAgency(User agency) {
        return repository.findByAgency(agency);
    }
}
