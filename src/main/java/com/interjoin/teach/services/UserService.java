package com.interjoin.teach.services;

import com.interjoin.teach.config.exceptions.EmailAlreadyExistsException;
import com.interjoin.teach.dtos.*;
import com.interjoin.teach.dtos.requests.AgencySignupRequest;
import com.interjoin.teach.dtos.requests.OtpVerifyRequest;
import com.interjoin.teach.dtos.requests.UpdateProfileRequest;
import com.interjoin.teach.dtos.responses.AuthResponse;
import com.interjoin.teach.dtos.responses.AvailableTimesSignupDto;
import com.interjoin.teach.dtos.responses.SignupResponseDto;
import com.interjoin.teach.entities.AvailableTimes;
import com.interjoin.teach.entities.SubjectCurriculum;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.mappers.UserMapper;
import com.interjoin.teach.repositories.SubjectCurriculumRepository;
import com.interjoin.teach.repositories.UserRepository;
import com.interjoin.teach.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final AwsService awsService;
    private final AvailableTimesService availableTimesService;
    private final ExperienceService experienceService;
    private final PaymentService paymentService;

    @Value("${verification.process.subject}")
    private String VERIFICATION_PROCESS_SUBJECT;

    private final SubjectCurriculumRepository subCurrRepository;

    public void createAgency(AgencySignupRequest request) {
        String usernameCreated = awsService.signUpAgency(request);
        User newAgency = User.builder()
                .agency(true)
                .email(request.getContactEmail())
                .additionalComments(request.getAdditionalComments())
                .agencyName(request.getAgencyName())
                .numberOfTeachers(request.getNumberOfTeachers())
                .location(request.getLocation())
                .cognitoUsername(usernameCreated)
                .role("AGENCY")
                .uuid(UUID.randomUUID().toString())
                .firstName(request.getAgencyName())
                .lastName(request.getAgencyName())
                .build();
        repository.save(newAgency);

    }

    public UserDto getCurrentUserAsDto() {
        return UserMapper.map(getCurrentUserDetails());
    }

    public UserDto updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUserDetails();
        if(StringUtils.isNotBlank(request.getPassword())) {
            System.out.println("Updating password");
            this.awsService.updateUserPassword(request.getPassword(), user.getCognitoUsername());
        }

        if(StringUtils.isNotBlank(request.getLocation())) {
            System.out.println("Updating location");
            user.setLocation(request.getLocation());
        }

        if(StringUtils.isNotBlank(request.getPhone())) {
            System.out.println("Updating phone");
            user.setPhoneNumber(request.getPhone());
        }

        if(StringUtils.isNotBlank(request.getShortBio())) {
            user.setShortBio(request.getShortBio());
        }

        if(StringUtils.isNotBlank(request.getLongBio())) {
            user.setLongBio(request.getLongBio());
        }

        if(Optional.ofNullable(request.getPricePerHour()).isPresent()) {
            user.setPricePerHour(request.getPricePerHour());

            BigDecimal percentage = request.getPricePerHour().multiply(BigDecimal.valueOf(22.5)).divide(BigDecimal.valueOf(100));
            user.setListedPrice(request.getPricePerHour().add(percentage));
        }

        if(Optional.ofNullable(request.getSubCurrList()).isPresent()) {
            Set<SubjectCurriculum> subCurrs = new HashSet<>();
            StringBuilder subCurrStr = new StringBuilder();

            for(SubjectCurriculumDto data : request.getSubCurrList()) {
                for(String subject : data.getSubjectNames()) {
                    SubjectCurriculum subjectCurriculum = subCurrRepository.findFirstByCurriculumCurriculumNameAndSubjectSubjectName(data.getCurriculumName(), subject);
                    subCurrs.add(subjectCurriculum);
                    subCurrStr.append(String.format("%s,%s", subject, data.getCurriculumName()));
                }

            }
            user.setSubjectCurriculums(subCurrs);
            user.setSubCurrStr(subCurrStr.toString());
        }

        if(Optional.ofNullable(request.getExperiences()).isPresent()) {
            //delete old experiences
            //add new ones
            experienceService.deleteForUser(user);
            experienceService.save(request.getExperiences(), user);
        }
        //delete old available times
//        availableTimesService.deleteAllByUser(user);
//        user.setAvailableTimes(availableTimesService.save(request.getAvailableTimes(),  user.getTimeZone(), user));
        return UserMapper.map(repository.save(user));
    }

    public SignupResponseDto createUser(UserSignupRequest request, String role) {
        User user = UserMapper.mapUserRequest(request);

        String usernameCreated = awsService.signUpUser(request, role);
        user.setCognitoUsername(usernameCreated);

        if(Optional.ofNullable(request.getSubCurrList()).isPresent()) {
            Set<SubjectCurriculum> subCurrs = new HashSet<>();
            StringBuilder subCurrStr = new StringBuilder();

            for(SubjectCurriculumDto data : request.getSubCurrList()) {
                for(String subject : data.getSubjectNames()) {
                    SubjectCurriculum subjectCurriculum = subCurrRepository.findFirstByCurriculumCurriculumNameAndSubjectSubjectName(data.getCurriculumName(), subject);
                    subCurrs.add(subjectCurriculum);
                    subCurrStr.append(String.format("%s,%s", subject, data.getCurriculumName()));
                }

            }
            user.setSubjectCurriculums(subCurrs);
            user.setSubCurrStr(subCurrStr.toString());
        }
        user.setUuid(UUID.randomUUID().toString());
        user.setRole(Optional.ofNullable(role.toUpperCase()).orElse("STUDENT"));
        user.setCreatedDate(LocalDateTime.now());
        user.setUuid(UUID.randomUUID().toString());
        user.setAgency(false);
        user = repository.save(user);

        // CHECK IF EXPERIENCE IS NULL
        if(Optional.ofNullable(request.getExperiences()).isPresent() && !request.getExperiences().isEmpty()) {
            experienceService.save(request.getExperiences(), user);
        }

        request = transformDays(request);

        // CHECK FOR AVAILABLE TIMES
        if(role.toUpperCase().equals("TEACHER")) {

            List<AvailableTimesDto>  avTimesDto = getAvailableTimes(request.getAvailableTimes(), request.getTimeZone());

            user.setAvailableTimes(availableTimesService.save(avTimesDto, user.getTimeZone(), user));
            // SET THE AGENCY
            user.setAgency(false);
            user.setAgencyName(getAgencyNameByReferalCode(request.getAgencyReferalCode()));
            user.setQualifications(request.getQualifications());
            user.setPricePerHour(request.getPricePerHour());
            BigDecimal percentage = request.getPricePerHour().multiply(BigDecimal.valueOf(22.5)).divide(BigDecimal.valueOf(100));
            user.setListedPrice(request.getPricePerHour().add(percentage));
        }
        user = repository.save(user);

        return SignupResponseDto.builder().firstName(user.getFirstName())
                .user(UserMapper.map(user))
                .lastName(user.getLastName())
                .uuid(user.getUuid())
                .cognitoUsername(user.getCognitoUsername())
                .subCurrList(request.getSubCurrList())
                .build();
    }

    public UserDto updateAvailableSlotsForCurrentTeacher(AvailableTimesSlots slots) {
        User currentTeacher = getCurrentUserDetails();
        availableTimesService.deleteAllByUser(currentTeacher);
        currentTeacher.setAvailableTimes(
                availableTimesService.save(getAvailableTimes(slots, currentTeacher.getTimeZone()), currentTeacher.getTimeZone(), currentTeacher)
        );
        return UserMapper.map(repository.save(currentTeacher));
    }

    private List<AvailableTimesDto> getAvailableTimes(AvailableTimesSlots availableTimes, String timezone) {
        AvailableTimesSignupDto av = new AvailableTimesSignupDto(timezone);
        List<AvailableTimesDto> avTimesDto = new ArrayList<>();
        if(availableTimes.getMon() != null) {
            List<Long> values = availableTimes.getMon();
            avTimesDto.add(AvailableTimesDto.builder()
                    .weekDay("monday")
                    .availableTimes(av.getAvailableTimes().stream()
                            .filter(avail -> values.contains(avail.getIndex())).map(AvailableHourMinuteDto::getDateTime).collect(Collectors.toList()))
                    .build());
        }

        if(availableTimes.getTue() != null) {
            List<Long> values = availableTimes.getTue();
            avTimesDto.add(AvailableTimesDto.builder()
                    .weekDay("tuesday")
                    .availableTimes(av.getAvailableTimes().stream()
                            .filter(avail -> values.contains(avail.getIndex())).map(AvailableHourMinuteDto::getDateTime).collect(Collectors.toList()))
                    .build());
        }

        if(availableTimes.getThu() != null) {
            List<Long> values = availableTimes.getThu();
            avTimesDto.add(AvailableTimesDto.builder()
                    .weekDay("thursday")
                    .availableTimes(av.getAvailableTimes().stream()
                            .filter(avail -> values.contains(avail.getIndex())).map(AvailableHourMinuteDto::getDateTime).collect(Collectors.toList()))
                    .build());
        }

        if(availableTimes.getWed() != null) {
            List<Long> values = availableTimes.getWed();
            avTimesDto.add(AvailableTimesDto.builder()
                    .weekDay("wednesday")
                    .availableTimes(av.getAvailableTimes().stream()
                            .filter(avail -> values.contains(avail.getIndex())).map(AvailableHourMinuteDto::getDateTime).collect(Collectors.toList()))
                    .build());
        }

        if(availableTimes.getFri() != null) {
            List<Long> values = availableTimes.getFri();
            avTimesDto.add(AvailableTimesDto.builder()
                    .weekDay("friday")
                    .availableTimes(av.getAvailableTimes().stream()
                            .filter(avail -> values.contains(avail.getIndex())).map(AvailableHourMinuteDto::getDateTime).collect(Collectors.toList()))
                    .build());
        }

        if(availableTimes.getSat() != null) {
            List<Long> values = availableTimes.getSat();
            avTimesDto.add(AvailableTimesDto.builder()
                    .weekDay("saturday")
                    .availableTimes(av.getAvailableTimes().stream()
                            .filter(avail -> values.contains(avail.getIndex())).map(AvailableHourMinuteDto::getDateTime).collect(Collectors.toList()))
                    .build());
        }

        if(availableTimes.getSun() != null) {
            List<Long> values = availableTimes.getSun();
            avTimesDto.add(AvailableTimesDto.builder()
                    .weekDay("sunday")
                    .availableTimes(av.getAvailableTimes().stream()
                            .filter(avail -> values.contains(avail.getIndex())).map(AvailableHourMinuteDto::getDateTime).collect(Collectors.toList()))
                    .build());
        }
        return avTimesDto;
    }

    private UserSignupRequest transformDays(UserSignupRequest request) {
//        for(AvailableTimesDto ava : request.getAvailableTimes()) {
//            switch (ava.getWeekDay()) {
//                case "Mon": {
//                    ava.setWeekDay("monday");
//                    break;
//                }
//                case "Tue": {
//                    ava.setWeekDay("tuesday");
//                    break;
//                }
//                case "Wed": {
//                    ava.setWeekDay("wednesday");
//                    break;
//                }
//                case "Thu": {
//                    ava.setWeekDay("thursday");
//                    break;
//                }
//                case "Fri": {
//                    ava.setWeekDay("friday");
//                    break;
//                }
//                case "Sat": {
//                    ava.setWeekDay("saturday");
//                    break;
//                }
//                case "Sun": {
//                    ava.setWeekDay("sunday");
//                    break;
//                }
//
//            }
//        }
        return request;
    }

    private org.springframework.security.core.userdetails.User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        return principal;
    }

    public UserDto getCurrentUserDetailsAsDto() {
        return UserMapper.map(getCurrentUserDetails());
    }

    public Optional<User> getUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    public User findById(Long id) {
        return repository.findById(id).get();
    }

    public AuthResponse signIn(UserSignInRequest request) {
        AuthResponse response = null;

        if(Optional.ofNullable(request).isPresent()) {
            response = awsService.signInUser(request);
            User user = getUserByEmail(request.getEmail()).orElseThrow(EntityNotFoundException::new);

            response.setUserDetails(UserMapper.map(user));
            response.setRole(Optional.ofNullable(user.getRole()).orElse(null));
        }
        return response;
    }

    public User getCurrentUserDetails() {
        org.springframework.security.core.userdetails.User principal = getCurrentUser();
        User currentUser = null;
        if(principal != null) {
            Optional<User> optionalUser = repository.findByCognitoUsername(principal.getUsername());
            if(optionalUser.isPresent())
                currentUser = optionalUser.get();
        }
        return currentUser;
    }

    public String getAgencyNameByReferalCode(String referalCode) {
        return repository.findFirstByAgencyCode(referalCode).map(User::getAgencyName).orElse(null);
    }

//    public List<AvailableTimesStringDto> getAvailableTimesForTeacher(Long teacherId) {
//        User teacher = findById(teacherId);
//        User currentStudent = getCurrentUserDetails();
//        List<AvailableTimes> times = availableTimesService.findByUser(teacher);
//
//        List<AvailableTimesStringDto> strings = DateUtils.map(times, currentStudent.getTimeZone());
//        strings.forEach(availableTimesStringDto -> {
//            availableTimesStringDto.setAvailableHourMinute(
//                    availableTimesStringDto.getAvailableHourMinute().stream().filter(specificDate -> {
//                        return specificDate.getDateTime().getDayOfWeek().toString().equals(availableTimesStringDto.getWeekDay().toUpperCase(Locale.ROOT));
//                    }).collect(Collectors.toList())
//            );
//
//        });
//
//        return strings;
//    }

    public AvailableTimesSlots getAvailableTimesForTeacher(Long teacherId) {
        User teacher = findById(teacherId);
//        User currentStudent = getCurrentUserDetails();

        AvailableTimesSlots returni = new AvailableTimesSlots();

        List<AvailableTimes> times = availableTimesService.findByUser(teacher);

        AvailableTimesSignupDto dto = new AvailableTimesSignupDto(teacher.getTimeZone());


        List<Long> mon = new ArrayList<>();
        Map<String, List<AvailableTimes>> avTimesMap = times.stream().collect(Collectors.groupingBy(AvailableTimes::getWeekDay));

        String teacherTimezone = teacher.getTimeZone();

        if(avTimesMap.containsKey("monday")) {
            returni.setMon(extractIndexes(avTimesMap.get("monday"), dto, teacherTimezone));
        }

        if(avTimesMap.containsKey("tuesday")) {
            returni.setTue(extractIndexes(avTimesMap.get("tuesday"), dto, teacherTimezone));
        }

        if(avTimesMap.containsKey("thursday")) {
            returni.setThu(extractIndexes(avTimesMap.get("thursday"), dto, teacherTimezone));
        }

        if(avTimesMap.containsKey("wednesday")) {
            returni.setWed(extractIndexes(avTimesMap.get("wednesday"), dto, teacherTimezone));
        }

        if(avTimesMap.containsKey("friday")) {
            returni.setFri(extractIndexes(avTimesMap.get("friday"), dto, teacherTimezone));
        }

        if(avTimesMap.containsKey("saturday")) {
            returni.setSat(extractIndexes(avTimesMap.get("saturday"), dto, teacherTimezone));
        }

        if(avTimesMap.containsKey("sunday")) {
            returni.setSun(extractIndexes(avTimesMap.get("sunday"), dto, teacherTimezone));
        }

        return returni;
    }

    public List<Long> extractIndexes(List<AvailableTimes> times, AvailableTimesSignupDto dto, String teacherTimezone) {
        List<Long> returns = new ArrayList<>();
        for(AvailableTimes time : times) {
            System.out.println("Without convert " + time.getDateTime());
            System.out.println("After map: " + DateUtils.map(time.getDateTime(), teacherTimezone, true));
            Long index = dto.getAvailableTimes().stream().filter(ex -> {
            OffsetDateTime date = DateUtils.map(time.getDateTime(), teacherTimezone, true);
            OffsetDateTime exDate = ex.getDateTime();
            return date.getHour() == exDate.getHour() && date.getMinute() == date.getMinute();
                    })
                    .findFirst().get().getIndex();
            returns.add(index);
        }

        return returns;
    }

    public List<AvailableTimesStringDto> getAvailableTimesForTeacherForDate(Long teacherId, LocalDate date) {
        User teacher = findById(teacherId);
        User currentStudent = getCurrentUserDetails();
        List<AvailableTimes> times = availableTimesService.findByUser(teacher);

        // we need to remove the booked ones


        List<AvailableTimesStringDto> strings = availableTimesService.findByTeacherAndSpecificDay(teacherId, date, currentStudent.getTimeZone());
        strings.forEach(availableTimesStringDto -> {
            availableTimesStringDto.setAvailableHourMinute(
                    availableTimesStringDto.getAvailableHourMinute().stream().filter(specificDate -> {
                        return specificDate.getDateTime().getDayOfWeek().toString().equals(availableTimesStringDto.getWeekDay().toUpperCase(Locale.ROOT));
                    }).collect(Collectors.toList())
            );

        });

        return strings;
    }

    public boolean emailAlreadyExists(String email) throws EmailAlreadyExistsException {
        if(repository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException();
        }
        return false;
    }

    public User findByUuid(String uuid) {
        return repository.findByUuid(uuid).orElseThrow(EntityNotFoundException::new);
    }

    public void verifyUser(OtpVerifyRequest request) {
        this.awsService.verifyUser(request.getCognitoUsername(), request.getOtpCode());
    }

    public void resendVerificationEmail(String cognitoUsername) {
        this.awsService.resendVerificationEmail(cognitoUsername);
    }

    public String purchaseVerification(String process) {
        BigDecimal price = BigDecimal.valueOf(60L);
        if(process.toLowerCase().equals("more")) {
            price = BigDecimal.valueOf(100L);
        }

        User currentTeacher = getCurrentUserDetails();

        Map<String, String> metadata = new HashMap<>();
        metadata.put("teacherId", String.valueOf(currentTeacher.getId()));

        return paymentService.openPaymentPage(price, VERIFICATION_PROCESS_SUBJECT, metadata, currentTeacher);
    }

    public void addProfilePictureToCurrentUser(MultipartFile picture, String userUuid) {
        User user = findByUuid(userUuid);
        try {
            user.setProfilePicture(IOUtils.toByteArray(picture.getInputStream()));
            repository.save(user);
        } catch (IOException e) {

        }
    }

    public void forgotPassword(String username) {
        this.awsService.forgotForUser(username);
    }

    public void resetPassword(ResetPasswordDTO request) throws IOException {
        this.awsService.resetUserPassword(request);
    }

    public void uploadCV(MultipartFile file, String userUuid) throws IOException {
//        User user = findByUuid(userUuid);
        this.awsService.uploadFile(file.getOriginalFilename(), file, User.builder().email("bytyqinderim87@gmail.com").build());
    }


    // TODO
    public void deleteAccount() {
        // CHECK IF THERE IS ANY SESSION LEFT FOR THE TEACHER
//        subCurrRepository.d
    }

    public Page<UserDto> getAgencyUsers(Pageable pageable) {
        User agencyUser = getCurrentUserDetails();
        return repository.findByAgencyAndAgencyName(false, agencyUser.getAgencyName(), pageable).map(UserMapper::map);
    }

    public void removeAgencyTeacher(Long teacherId) {
        User currentAgency = getCurrentUserDetails();
        User teacher = repository.findById(teacherId).orElseThrow(EntityNotFoundException::new);

        // CHECK IF USER IS ON CURRENT AGENCY
        if(!teacher.getAgencyName().equals(currentAgency.getAgencyName())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        teacher.setAgencyName(null);
        repository.save(teacher);
    }
}

