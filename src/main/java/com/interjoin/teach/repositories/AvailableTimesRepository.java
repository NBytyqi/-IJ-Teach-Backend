package com.interjoin.teach.repositories;

import com.interjoin.teach.entities.AvailableTimes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailableTimesRepository extends JpaRepository<AvailableTimes, Long> {

}
