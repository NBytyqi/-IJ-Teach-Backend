package com.interjoin.teach.services;

import com.interjoin.teach.config.exceptions.EmailAlreadyExistsException;
import com.interjoin.teach.dtos.*;
import com.interjoin.teach.dtos.requests.AgencySignupRequest;
import com.interjoin.teach.dtos.requests.OtpVerifyRequest;
import com.interjoin.teach.dtos.responses.AuthResponse;
import com.interjoin.teach.dtos.responses.SignupResponseDto;
import com.interjoin.teach.entities.AvailableTimes;
import com.interjoin.teach.entities.SubjectCurriculum;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.mappers.UserMapper;
import com.interjoin.teach.repositories.SubjectCurriculumRepository;
import com.interjoin.teach.repositories.UserRepository;
import com.interjoin.teach.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final AwsService awsService;
    private final AvailableTimesService availableTimesService;
    private final ExperienceService experienceService;

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

        // CHECK FOR AVAILABLE TIMES
        if(role.toUpperCase().equals("TEACHER")) {
            user.setAvailableTimes(availableTimesService.save(request.getAvailableTimes(), user.getTimeZone(), user.getId()));
        }
        repository.save(user);

        return SignupResponseDto.builder().firstName(user.getFirstName())
                .lastName(user.getLastName())
                .uuid(user.getUuid())
                .cognitoUsername(user.getCognitoUsername()).build();
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
            Optional<User> optionalUser = getUserByEmail(request.getEmail());

            if(optionalUser.isPresent()) {
                response = awsService.signInUser(request);
                response.setUserDetails(UserMapper.map(optionalUser.get()));
            }
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

    public List<AvailableTimesStringDto> getAvailableTimesForTeacher(Long teacherId) {
        User teacher = findById(teacherId);
        User currentStudent = getCurrentUserDetails();
        List<AvailableTimes> times = availableTimesService.findByUser(teacher);

        List<AvailableTimesStringDto> strings = DateUtils.map(times, currentStudent.getTimeZone());
        strings.forEach(availableTimesStringDto -> {
            availableTimesStringDto.setAvailableHourMinute(
                    availableTimesStringDto.getAvailableHourMinute().stream().filter(specificDate -> {
                        return specificDate.getDateTime().getDayOfWeek().toString().equals(availableTimesStringDto.getWeekDay().toUpperCase(Locale.ROOT));
                    }).collect(Collectors.toList())
            );

        });

        return strings;
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

    public void addProfilePictureToCurrentUser(MultipartFile picture, String userUuid) {
        User user = findByUuid(userUuid);
        try {
            user.setProfilePicture(picture.getBytes());
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
}

