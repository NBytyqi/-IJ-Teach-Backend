package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.SessionDto;
import com.interjoin.teach.entities.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SessionMapper {

    public static SessionDto map(Session session) {
        return SessionDto.builder()
                .teacherFullName(Optional.ofNullable(session.getTeacher()).map(teacher -> String.format("%s %s", teacher.getFirstName(), teacher.getLastName())).orElse(null))
                .studentFullName(Optional.ofNullable(session.getStudent()).map(student -> String.format("%s %s", student.getFirstName(), student.getLastName())).orElse(null))
                .date(session.getDateSlot())
                .build();
    }

    public static List<SessionDto> map(List<Session> sessions) {
        List<SessionDto> sessionDtos = new ArrayList<>();
        for(Session session : sessions) {
            sessionDtos.add(map(session));
        }
        return sessionDtos;
    }
}
