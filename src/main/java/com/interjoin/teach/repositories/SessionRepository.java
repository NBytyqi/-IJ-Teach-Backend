package com.interjoin.teach.repositories;

import com.interjoin.teach.entities.Session;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.enums.SessionStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByUuid(String uuid);

    Optional<Session> findByUuidAndTeacher(String uuid, User teacher);
    Optional<Session> findByUuidAndStudentAndSessionStatus(String uuid, User student, SessionStatus sessionStatus);

    Optional<Session> findByTeacherAndDateSlotAndSessionStatusNot(User teacher, OffsetDateTime dateSlot, SessionStatus status);

    List<Session> findByTeacherOrderByDateSlotDesc(User teacher);

    @Query(value = "SELECT s from Session s WHERE DATE(s.dateSlot) = :date AND s.teacher.id = :teacherId AND s.sessionStatus != :status")
    List<Session> findByTeacherAndSpecificDateAndStatusNot(@Param("teacherId") Long teacherId, @Param("date") LocalDate date, @Param("status") SessionStatus status);

    List<Session> findByStudentAndDateSlotBefore(User currentStudent, OffsetDateTime today, Pageable pageable);
    List<Session> findByStudentAndDateSlotAfterAndSessionStatus(User currentStudent, OffsetDateTime today, SessionStatus status, Pageable pageable);
    List<Session> findByTeacherAndDateSlotBefore(User currentTeacher, OffsetDateTime today, Pageable pageable);
    List<Session> findByTeacherAndDateSlotAfter(User currentTeacher, OffsetDateTime today, Pageable pageable);
    List<Session> findByTeacherAndDateSlotAfterAndSessionStatus(User currentTeacher, OffsetDateTime today, SessionStatus status, Pageable pageable);

    @Query(value = "SELECT s from Session s where (s.student = :currentUser or s.teacher = :currentUser) AND s.dateSlot >= :today ")
    List<Session> findByStudentOrTeacherAndDateSlotAfter(@Param("currentUser") User currentUser, @Param("today") OffsetDateTime today);

    @Query(value = "DELETE from Session where teacher = :user OR student = :user")
    @Modifying
    void deleteUserSessions(@Param("user") User user);
}
