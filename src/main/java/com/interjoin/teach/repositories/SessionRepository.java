package com.interjoin.teach.repositories;

import com.interjoin.teach.entities.Session;
import com.interjoin.teach.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByTeacherAndStudentAndDateSlot(User teacher, User student, OffsetDateTime dateSlot);


    List<Session> findByTeacherOrderByDateSlotDesc(User teacher);
}
