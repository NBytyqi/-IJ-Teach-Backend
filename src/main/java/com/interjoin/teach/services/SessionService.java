package com.interjoin.teach.services;

import com.interjoin.teach.config.exceptions.SessionExistsException;
import com.interjoin.teach.dtos.AvailableHourMinuteDto;
import com.interjoin.teach.dtos.AvailableTimesStringDto;
import com.interjoin.teach.dtos.SessionDto;
import com.interjoin.teach.dtos.requests.BookSessionRequest;
import com.interjoin.teach.entities.AvailableTimes;
import com.interjoin.teach.entities.Session;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.enums.SessionStatus;
import com.interjoin.teach.mappers.SessionMapper;
import com.interjoin.teach.repositories.SessionRepository;
import com.interjoin.teach.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final UserService userService;
    private final AvailableTimesService timesService;

    private final SessionRepository sessionRepository;
    private final PaymentService paymentService;

    public String bookSession(BookSessionRequest request) throws SessionExistsException {
        OffsetDateTime requestedBookTime = request.getDate().getDateTime();
        request.getDate().setDateTime(OffsetDateTime.from(requestedBookTime.atZoneSameInstant(ZoneId.systemDefault())));
        User teacher = userService.findById(request.getTeacherId());
        User student = userService.getCurrentUserDetails();

        String weekDay = request.getDate().getDateTime().getDayOfWeek().toString().toLowerCase(Locale.ROOT);
        // ALL available times for that day, filtering them on the day
        List<AvailableTimes> teacherAvailableTimes = filterAvailableTimes(
                timesService.findByUserAndWeekDay(request.getTeacherId(), weekDay), weekDay
        );

        Optional<Session> optionalSession = sessionRepository.findByTeacherAndStudentAndDateSlot(teacher, student, request.getDate().getDateTime());
        if(optionalSession.isPresent() || !isTeacherAvailable(teacherAvailableTimes, request.getDate().getDateTime())) {
            throw new SessionExistsException("This slot is busy");
        }

        Session session = Session.builder()
                                 .teacher(teacher)
                                 .student(student)
                                 .dateSlot(request.getDate().getDateTime())
                                 .sessionStatus(SessionStatus.PAYMENT_PENDING)
                                 .build();
        session = sessionRepository.save(session);

        return paymentService.openPaymentPage(request, session.getId(), teacher.getPricePerHour(), student.getFirstName(), teacher.getFirstName(), "Math", "A1");
    }


    public List<SessionDto> getCurrentTeacherSessions() {
        User teacher = userService.getCurrentUserDetails();
        return SessionMapper.map(sessionRepository.findByTeacherOrderByDateSlotDesc(teacher));
    }

    private List<AvailableTimes> filterAvailableTimes(List<AvailableTimes> availableTimes, String weekDay) {
        return availableTimes.stream().filter(date -> {
            return date.getDateTime().getDayOfWeek().toString().toLowerCase(Locale.ROOT).equals(weekDay);
        }).collect(Collectors.toList());
    }

    private boolean isTeacherAvailable(List<AvailableTimes> avTimes, OffsetDateTime dateTime) {
        for(AvailableTimes av : avTimes) {
            if(av.getWeekDay().equals(dateTime.getDayOfWeek().toString().toLowerCase(Locale.ROOT)) &&
                    av.getDateTime().getHour() == dateTime.getHour() &&
                    av.getDateTime().getMinute() == dateTime.getMinute()) {
                return true;
            }
        }
        return false;
    }

    public List<AvailableHourMinuteDto> availableTimesPerDay(Long teacherId, LocalDate date) {

        List<AvailableTimesStringDto> avTimesInStudentTimezone = userService.getAvailableTimesForTeacherForDate(teacherId, date);

        List<OffsetDateTime> bookedSessions = DateUtils.mapMultipleTimes(sessionRepository.findByTeacherAndSpecificDate(teacherId, date).stream().map(session -> session.getDateSlot()).collect(Collectors.toList()), userService.getCurrentUserDetails().getTimeZone());

        if(avTimesInStudentTimezone.isEmpty()) {
            return new ArrayList<>();
        }

        return avTimesInStudentTimezone.get(0).getAvailableHourMinute().stream().filter(avTime -> !bookedSessions.contains(avTime.getDateTime())).collect(Collectors.toList());
    }
}
