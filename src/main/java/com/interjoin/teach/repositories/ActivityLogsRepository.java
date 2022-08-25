package com.interjoin.teach.repositories;

import com.interjoin.teach.entities.ActivityLogs;
import com.interjoin.teach.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogsRepository extends JpaRepository<ActivityLogs, Long> {

    List<ActivityLogs> findByAgency(User agency);
}
