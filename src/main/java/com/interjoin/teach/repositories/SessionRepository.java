package com.interjoin.teach.repositories;

import com.interjoin.teach.entities.Session;
import com.interjoin.teach.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByTeacherAndStudentAndDateSlot(User teacher, User student, OffsetDateTime dateSlot);

    List<Session> findByTeacherOrderByDateSlotDesc(User teacher);

    @Query(value = "SELECT s from Session s WHERE DATE(s.dateSlot) = :date AND s.teacher.id = :teacherId")
    List<Session> findByTeacherAndSpecificDate(@Param("teacherId") Long teacherId, @Param("date") LocalDate date);

    List<Session> findByStudentAndDateSlotBefore(User currentStudent, OffsetDateTime today, Pageable pageable);
}
