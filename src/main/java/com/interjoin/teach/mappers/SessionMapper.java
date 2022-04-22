package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.SessionDto;
import com.interjoin.teach.entities.Session;
import com.interjoin.teach.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SessionMapper {

    public static SessionDto map(Session session, String userTimezone) {
        return SessionDto.builder()
                .teacher(Optional.ofNullable(session.getTeacher()).map(UserMapper::map).orElse(null))
                .studentFullName(Optional.ofNullable(session.getStudent()).map(student -> String.format("%s %s", student.getFirstName(), student.getLastName())).orElse(null))
                .date(DateUtils.map(session.getDateSlot(), userTimezone))
                .review(session.getReview())
                .reviewScore(session.getReviewScore())
                .build();
    }

    public static List<SessionDto> map(List<Session> sessions, String timeZone) {
        List<SessionDto> sessionDtos = new ArrayList<>();
        for(Session session : sessions) {
            sessionDtos.add(map(session, timeZone));
        }
        return sessionDtos;
    }
}
