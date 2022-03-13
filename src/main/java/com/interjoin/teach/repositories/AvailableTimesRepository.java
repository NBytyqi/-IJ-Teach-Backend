package com.interjoin.teach.repositories;

import com.interjoin.teach.entities.AvailableTimes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AvailableTimesRepository extends JpaRepository<AvailableTimes, Long> {

    List<AvailableTimes> findByTeacherIdAndWeekDay(Long teacherId, String weekDay);
}
