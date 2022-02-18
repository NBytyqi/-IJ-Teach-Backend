package com.interjoin.teach.services;

import com.interjoin.teach.dtos.SubjectCurriculumDto;
import com.interjoin.teach.dtos.SubjectCurriculumResponse;
import com.interjoin.teach.dtos.UserSignInRequest;
import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.dtos.responses.AuthResponse;
import com.interjoin.teach.entities.SubjectCurriculum;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.mappers.UserMapper;
import com.interjoin.teach.repositories.SubjectCurriculumRepository;
import com.interjoin.teach.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final AwsService awsService;
    private final SubjectCurriculumRepository subCurrRepository;

    public void createUser(UserSignupRequest request, String role) {
        User user = UserMapper.mapUserRequest(request);

        String usernameCreated = awsService.signUpUser(request, role);
        user.setUsername(usernameCreated);

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

        repository.save(user);

    }

    private org.springframework.security.core.userdetails.User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        return principal;
    }

    public Optional<User> getUserByEmail(String email) {
        return repository.findByEmail(email);
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
}

