package com.interjoin.teach.services;

import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.config.exceptions.SessionNotValidException;
import com.interjoin.teach.dtos.AvailableHourMinuteDto;
import com.interjoin.teach.dtos.AvailableTimesStringDto;
import com.interjoin.teach.dtos.EmailDTO;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    @Value("${spring.sendgrid.templates.teacher-absent}")
    private String teacherAbsentTemplate;

    @Value("${spring.sendgrid.templates.teacher-absent-to-teacher-template}")
    private String teacherAbsentToTeacherTemplate;

    @Value("${spring.sendgrid.templates.teacher-approve-session}")
    private String approveSessionTemplate;

    @Value("${spring.sendgrid.templates.teacher-decline-session}")
    private String declineSessionTemplate;

    @Value("${spring.sendgrid.templates.session-confirmation-template}")
    private String sessionConfirmationTemplate;

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mma ('UTC' xxx)");



    private final String FIRST_NAME = "firstName";
    private final String TEACHER_FIRST_NAME = "teacherName";
    private final String STUDENT_FIRST_NAME = "studentName";
    private final String DATE = "date";
    private final String TIME = "time";
    private final String CURRICULUM = "curriculum";
    private final String SUBJECT = "subject";
    private final String COMMENT = "comment";
    private final String PRICE = "price";

    private final EmailService emailService;
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
                                 .subject(request.getSubject())
                                 .curriculum(request.getCurriculum())
                                 .price(teacher.getListedPrice())
                                 .comment(request.getComment())
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

    public Session findById(Long sessionId) throws InterjoinException {
        return sessionRepository.findById(sessionId).orElseThrow(() -> new InterjoinException("Session not found", HttpStatus.NOT_FOUND));
    }

    public void update(Session session) {
        sessionRepository.save(session);
    }

    public List<SessionDto> getCurrentTeacherSessions() {
        User teacher = userService.getCurrentUserDetails();
        return SessionMapper.map(sessionRepository.findByTeacherOrderByDateSlotDesc(teacher), teacher.getTimeZone());
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

    public List<SessionDto> getStudentSessionRequests(Pageable pageable) {
        User currentStudent = userService.getCurrentUserDetails();
        return sessionRepository.findByStudentAndDateSlotAfterAndSessionStatus(currentStudent, OffsetDateTime.now(), SessionStatus.PENDING_APPROVAL, pageable)
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

    public List<SessionDto> getTeacherActiveSessions(Pageable pageable) {
        User currentTeacher = userService.getCurrentUserDetails();
        return sessionRepository.findByTeacherAndDateSlotAfterAndSessionStatus(currentTeacher, OffsetDateTime.now(), SessionStatus.APPROVED, pageable)
                .stream().map(session -> SessionMapper.map(session, currentTeacher.getTimeZone()))
                .collect(Collectors.toList());
    }

    public List<SessionDto> getTeacherSessionRequests(Pageable pageable) {
        User currentTeacher = userService.getCurrentUserDetails();
        return sessionRepository.findByTeacherAndDateSlotAfterAndSessionStatus(currentTeacher, OffsetDateTime.now(), SessionStatus.PENDING_APPROVAL, pageable)
                .stream().map(session -> SessionMapper.map(session, currentTeacher.getTimeZone()))
                .collect(Collectors.toList());
    }

    public List<SessionDto> approveSession(String sessionUuid, boolean approve) throws SessionNotValidException {
        User teacher = userService.getCurrentUserDetails();
        Session session = sessionRepository.findByUuidAndTeacher(sessionUuid, teacher).orElseThrow(() -> new SessionNotValidException("Session is not found"));

        if(session.getDateSlot().isBefore(OffsetDateTime.now())) {
            throw new SessionNotValidException("Session is expired");
        }


        SessionStatus status = approve ? SessionStatus.APPROVED : SessionStatus.DECLINED;

        // TODO if approve is false, refund the payment

        session.setSessionStatus(status);
        sessionRepository.save(session);

        User student = session.getStudent();

        Map<String, String> templateKeys = new HashMap<>();
        templateKeys.put(FIRST_NAME, student.getFirstName());
        templateKeys.put(TEACHER_FIRST_NAME, teacher.getFirstName());
        templateKeys.put(DATE, DateUtils.map(session.getDateSlot(), student.getTimeZone()).format(dateFormatter));
        templateKeys.put(TIME, DateUtils.map(session.getDateSlot(), student.getTimeZone()).format(timeFormatter));
        templateKeys.put(CURRICULUM, session.getCurriculum());
        templateKeys.put(SUBJECT, session.getSubject());
        templateKeys.put(PRICE,  session.getPrice() + "$");
        templateKeys.put(COMMENT, session.getComment());

        EmailDTO emailDTO = EmailDTO.builder()
                .toEmail(student.getEmail())
                .templateId(approve ? approveSessionTemplate : declineSessionTemplate)
                .templateKeys(templateKeys)
                .build();
        emailService.sendEmail(emailDTO);

        // send session confirmation email

        if(approve) {

            Map<String, String> templateKeysForTeacher = new HashMap<>();
            templateKeysForTeacher.put(FIRST_NAME, teacher.getFirstName());
            templateKeysForTeacher.put(STUDENT_FIRST_NAME, student.getFirstName());
            templateKeysForTeacher.put(DATE, DateUtils.map(session.getDateSlot(), teacher.getTimeZone()).format(dateFormatter));
            templateKeysForTeacher.put(TIME, DateUtils.map(session.getDateSlot(), teacher.getTimeZone()).format(timeFormatter));
            templateKeysForTeacher.put(CURRICULUM, session.getCurriculum());
            templateKeysForTeacher.put(SUBJECT, session.getSubject());
            templateKeys.put(COMMENT, session.getComment());
            EmailDTO emailDTOForTeacher = EmailDTO.builder()
                    .toEmail(teacher.getEmail())
                    .templateId(sessionConfirmationTemplate)
                    .templateKeys(templateKeysForTeacher)
                    .build();
            emailService.sendEmail(emailDTOForTeacher);
        }

        return getTeacherSessionRequests(Pageable.unpaged());
    }

    public void paymentSuccessfullyDone(Long sessionId, String chargeid) throws InterjoinException {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new InterjoinException("Session doesn't exist"));
        session.setSessionStatus(SessionStatus.PENDING_APPROVAL);
        sessionRepository.save(session);
    }

    @Value("${spring.sendgrid.templates.session-completed}")
    private String studentSessionCompletedTemplate;
    @Value("${spring.sendgrid.templates.session-finished-template}")
    private String teacherSessionCompletedTemplate;
    @Transactional
    public void markSessionAsFinished(String sessionUuid) throws InterjoinException {
        User teacher = userService.getCurrentUserDetails();
        Session session = sessionRepository.findByUuidAndTeacher(sessionUuid, teacher).orElseThrow(() -> new InterjoinException("Session with uuid doesn't exist"));
        session.setSessionStatus(SessionStatus.FINISHED);
        // send session completed email
        User student = session.getStudent();
        Map<String, String> templateKeys = new HashMap<>();
        templateKeys.put(FIRST_NAME, student.getFirstName());
        templateKeys.put(TEACHER_FIRST_NAME, teacher.getFirstName());
        EmailDTO emailDTO = EmailDTO.builder()
                .toEmail(student.getEmail())
                .templateId(studentSessionCompletedTemplate)
                .templateKeys(templateKeys)
                .build();
        emailService.sendEmail(emailDTO);

        // send email to teacher
        Map<String, String> templateKeysForTeacher = new HashMap<>();
        templateKeysForTeacher.put(FIRST_NAME, teacher.getFirstName());
        EmailDTO emailDTOForTeacher = EmailDTO.builder()
                .toEmail(teacher.getEmail())
                .templateId(teacherSessionCompletedTemplate)
                .templateKeys(templateKeysForTeacher)
                .build();
        emailService.sendEmail(emailDTOForTeacher);

    }

    @Transactional
    public void markTeacherAsAbsent(String sessionUuid) throws InterjoinException {
        User currentStudent = userService.getCurrentUserDetails();

        Session session = sessionRepository.findByUuidAndStudentAndSessionStatus(sessionUuid, currentStudent, SessionStatus.APPROVED).orElseThrow(() -> new InterjoinException("Session with uuid doesn't exist"));
        session.setSessionStatus(SessionStatus.TEACHER_ABSENT);

        User teacher = session.getTeacher();

        //send email to student to mark teacher as absent
        Map<String, String> templateKeys = new HashMap<>();
        templateKeys.put(FIRST_NAME, currentStudent.getFirstName());
        templateKeys.put(TEACHER_FIRST_NAME, session.getTeacher().getFirstName());
        EmailDTO emailDTO = EmailDTO.builder()
                .toEmail(currentStudent.getEmail())
                .templateId(teacherAbsentTemplate)
                .templateKeys(templateKeys)
                .build();
        emailService.sendEmail(emailDTO);

        //send email to teacher to mark teacher as absent
        Map<String, String> templateKeysForTeacher = new HashMap<>();
        templateKeysForTeacher.put(FIRST_NAME, teacher.getFirstName());
        templateKeysForTeacher.put(STUDENT_FIRST_NAME, currentStudent.getFirstName());
        templateKeysForTeacher.put(DATE, DateUtils.map(session.getDateSlot(), teacher.getTimeZone()).format(dateFormatter));
        templateKeysForTeacher.put(TIME, DateUtils.map(session.getDateSlot(), teacher.getTimeZone()).format(timeFormatter));
        templateKeysForTeacher.put(CURRICULUM, session.getCurriculum());
        templateKeysForTeacher.put(SUBJECT, session.getSubject());
        templateKeys.put(COMMENT, session.getComment());
        EmailDTO emailDTOForTeacher = EmailDTO.builder()
                .toEmail(teacher.getEmail())
                .templateId(teacherAbsentToTeacherTemplate)
                .templateKeys(templateKeysForTeacher)
                .build();
        emailService.sendEmail(emailDTOForTeacher);
    }
}
