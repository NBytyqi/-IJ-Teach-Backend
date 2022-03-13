package com.interjoin.teach.services;

import com.interjoin.teach.dtos.*;
import com.interjoin.teach.dtos.responses.AuthResponse;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final AwsService awsService;
    private final AvailableTimesService availableTimesService;

    private final SubjectCurriculumRepository subCurrRepository;

    public void createUser(UserSignupRequest request, String role) {
        User user = UserMapper.mapUserRequest(request);

//        String usernameCreated = awsService.signUpUser(request, role);
//        user.setUsername(usernameCreated);

        if(Optional.ofNullable(request.getSubCurrList()).isPresent()) {
            Set<SubjectCurriculum> subCurrs = new HashSet<>();
            StringBuilder subCurrStr = new StringBuilder();

            for(SubjectCurriculumDto data : request.getSubCurrList()) {
                SubjectCurriculum subjectCurriculum = subCurrRepository.findFirstByCurriculumCurriculumNameAndSubjectSubjectName(data.getCurriculumName(), data.getSubjectName());
                subCurrs.add(subjectCurriculum);
                subCurrStr.append(String.format("%s,%s", data.getSubjectName(), data.getCurriculumName()));
            }
            user.setSubjectCurriculums(subCurrs);
            user.setSubCurrStr(subCurrStr.toString());
        }
        user.setUuid(UUID.randomUUID().toString());
        user.setRole(Optional.ofNullable(role.toUpperCase()).orElse("STUDENT"));
        user.setCreatedDate(LocalDateTime.now());

        // CHECK FOR AVAILABLE TIMES
        if(role.toUpperCase().equals("TEACHER")) {
            user.setAvailableTimes(availableTimesService.save(request.getAvailableTimes(), user.getTimeZone()));
        }

        repository.save(user);

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
            Optional<User> optionalUser = repository.findByUsername(principal.getUsername());
            if(optionalUser.isPresent())
                currentUser = optionalUser.get();
        }
        return currentUser;
    }

    public List<AvailableTimesStringDto> getAvailableTimesForTeacher(Long teacherId) {
        User teacher = findById(teacherId);
//        User currentStudent = getCurrentUserDetails();
        List<AvailableTimes> times = availableTimesService.findByUser(teacher);

        List<AvailableTimesStringDto> strings = DateUtils.map(times, "Europe/Belgrade");
        strings.forEach(availableTimesStringDto -> {
            availableTimesStringDto.setAvailableHourMinute(
                    availableTimesStringDto.getAvailableHourMinute().stream().filter(specificDate -> {
                        return specificDate.getDateTime().getDayOfWeek().toString().equals(availableTimesStringDto.getWeekDay().toUpperCase(Locale.ROOT));
                    }).collect(Collectors.toList())
            );

        });

        return strings;
    }
}

