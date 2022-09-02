package com.interjoin.teach.services;

import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.config.exceptions.SessionNotValidException;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final UserService userService;
    private final AvailableTimesService timesService;

    private final SessionRepository sessionRepository;
    private final PaymentService paymentService;

    public String bookSession(BookSessionRequest request) throws InterjoinException {

        LocalDateTime now = LocalDateTime.now();
        ZoneId zone = ZoneId.of(userService.getCurrentUserDetails().getTimeZone());
        ZoneOffset zoneOffSet = zone.getRules().getOffset(now);
        OffsetDateTime requestedBookTime = request.getDate().getDateTime();
            request.getDate().setDateTime(OffsetDateTime.from(requestedBookTime.atZoneSameInstant(zoneOffSet)));

        // DATE OF SESSION
        LocalDate dateOfSession = request.getDate().getDateOfSession();
        request.getDate().setDateTime(
                request.getDate().getDateTime()
                        .withYear(dateOfSession.getYear())
                        .withMonth(dateOfSession.getMonthValue())
                        .withDayOfMonth(dateOfSession.getDayOfMonth())
        );

        User teacher = userService.findById(request.getTeacherId());
        User student = userService.getCurrentUserDetails();

        String weekDay = request.getDate().getDateTime().getDayOfWeek().toString().toLowerCase(Locale.ROOT);
        // ALL available times for that day, filtering them on the day
        List<AvailableTimes> teacherAvailableTimes = filterAvailableTimes(
                timesService.findByUserAndWeekDay(request.getTeacherId(), weekDay), weekDay
        );

        Optional<Session> optionalSession = sessionRepository.findByTeacherAndDateSlotAndSessionStatusNot(teacher, request.getDate().getDateTime(), SessionStatus.DECLINED);
        if(optionalSession.isPresent() || !isTeacherAvailable(teacherAvailableTimes, request.getDate().getDateTime())) {
            throw new InterjoinException("This slot is busy");
        }

        Session session = Session.builder()
                                 .teacher(teacher)
                                 .student(student)
                                 .dateSlot(request.getDate().getDateTime())
                                 .sessionStatus(SessionStatus.PAYMENT_PENDING)
                                 .price(teacher.getListedPrice())
                .uuid(UUID.randomUUID().toString())
                                 .build();
        session = sessionRepository.save(session);

       final String subject = String.format("Session between %s and %s on subject: \"%s\" and Curriculum: \"%s\"", student.getFirstName(), teacher.getFirstName(), request.getSubject(), request.getCurriculum());

       Map<String, String> metadata = new HashMap<>();
       metadata.put("sessionId", String.valueOf(session.getId()));

       String urlParams = String.format("/booking-status?teacherId=%d&subject=%s&curriculum=%s&time=%s&date=%s&status=PAID", teacher.getId(), request.getSubject(), request.getCurriculum(), request.getDate().getHourMinuteString(), request.getDate().getDateOfSession().toString());

       return paymentService.openPaymentPage(teacher.getListedPrice(), subject, metadata, userService.getCurrentUserDetails(), urlParams);
    }

    public Session findByUuid(String uuid) throws InterjoinException {
        return sessionRepository.findByUuid(uuid).orElseThrow(() -> new InterjoinException("Session not found", HttpStatus.NOT_FOUND));
    }


    public List<SessionDto> getCurrentTeacherSessions() {
        User teacher = userService.getCurrentUserDetails();
        return SessionMapper.map(sessionRepository.findByTeacherOrderByDateSlotDesc(teacher), teacher.getTimeZone());
    }


    // GET TEACHER/STUDENT SESSIONS FROM CURRENT DATE TIME AND ON
    // TODO - Refactor method to user service
    public void deleteCurrentUser() throws Exception {
        User user = userService.getCurrentUserDetails();
        List<Session> slots = sessionRepository.findByStudentOrTeacherAndDateSlotAfter(user, OffsetDateTime.now().minusDays(1));
        if(!slots.isEmpty()) {
            throw new Exception("User cannot be deleted since it has active session or future sessions");
        }
        sessionRepository.deleteUserSessions(user);
        userService.deleteAccount();


    }

    private List<AvailableTimes> filterAvailableTimes(List<AvailableTimes> availableTimes, String weekDay) {
        return availableTimes.stream().filter(date -> {
            return date.getDateTime().getDayOfWeek().toString().toLowerCase(Locale.ROOT).equals(weekDay);
        }).collect(Collectors.toList());
    }

    private boolean isTeacherAvailable(List<AvailableTimes> avTimes, OffsetDateTime dateTime) {
        for(AvailableTimes av : avTimes) {
            LocalDateTime now = LocalDateTime.now();
            ZoneId zone = ZoneId.systemDefault();
            ZoneOffset zoneOffSet = zone.getRules().getOffset(now);
            dateTime = OffsetDateTime.from(dateTime.withOffsetSameInstant(zoneOffSet));
            av.setDateTime(dateTime);
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

        List<LocalTime> bookedSessions = DateUtils.mapMultipleTimes(sessionRepository.findByTeacherAndSpecificDateAndStatusNot(teacherId, date, SessionStatus.DECLINED).stream().map(session -> session.getDateSlot()).collect(Collectors.toList()), userService.getCurrentUserDetails().getTimeZone())
                .stream().map(of -> of.toLocalDateTime().toLocalTime()).collect(Collectors.toList());

        if(avTimesInStudentTimezone.isEmpty()) {
            return new ArrayList<>();
        }

        return avTimesInStudentTimezone.get(0).getAvailableHourMinute().stream().filter(avTime -> !bookedSessions.contains(avTime.getDateTime().toLocalDateTime().toLocalTime())).collect(Collectors.toList());
    }

    public List<SessionDto> getStudentSessionClasses(Pageable pageable) {
        User currentStudent = userService.getCurrentUserDetails();
        return sessionRepository.findByStudentAndDateSlotAfterAndSessionStatus(currentStudent, OffsetDateTime.now(), SessionStatus.APPROVED, pageable)
                .stream().map(session -> SessionMapper.map(session, currentStudent.getTimeZone()))
                .collect(Collectors.toList());
    }

    public List<SessionDto> getStudentSessionHistory(Pageable pageable) {
        User currentStudent = userService.getCurrentUserDetails();
        return sessionRepository.findByStudentAndDateSlotBefore(currentStudent, OffsetDateTime.now(), pageable)
                                .stream().map(session -> SessionMapper.map(session, currentStudent.getTimeZone()))
                                .collect(Collectors.toList());
    }

    public List<SessionDto> getTeacherSessionHistory(Pageable pageable) {
        User currentTeacher = userService.getCurrentUserDetails();
        return sessionRepository.findByTeacherAndDateSlotBefore(currentTeacher, OffsetDateTime.now(), pageable)
                .stream().map(session -> SessionMapper.map(session, currentTeacher.getTimeZone()))
                .collect(Collectors.toList());
    }

    public List<SessionDto> getTeacherSessionRequests(Pageable pageable) {
        User currentTeacher = userService.getCurrentUserDetails();
        return sessionRepository.findByTeacherAndDateSlotAfterAndSessionStatus(currentTeacher, OffsetDateTime.now(), SessionStatus.PENDING_APPROVAL, pageable)
                .stream().map(session -> SessionMapper.map(session, currentTeacher.getTimeZone()))
                .collect(Collectors.toList());
    }

    public void approveSession(String sessionUuid, boolean approve) throws SessionNotValidException {
        User teacher = userService.getCurrentUserDetails();
        Session session = sessionRepository.findByUuidAndTeacher(sessionUuid, teacher).orElseThrow(() -> new SessionNotValidException("Session is not found"));

        if(session.getDateSlot().isBefore(OffsetDateTime.now())) {
            throw new SessionNotValidException("Session is expired");
        }

        SessionStatus status = approve ? SessionStatus.APPROVED : SessionStatus.DECLINED;

        session.setSessionStatus(status);
        sessionRepository.save(session);
    }
}
