package com.interjoin.teach.services;

import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.entities.SubjectCurriculum;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.mappers.UserMapper;
import com.interjoin.teach.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public void createUser(UserSignupRequest request, String role) {
        User user = UserMapper.mapUserRequest(request);

        if(Optional.ofNullable(request.getSubCurrList()).isPresent()) {
            Set<SubjectCurriculum> courses = request.getSubCurrList();
            StringBuilder subCurrStr = new StringBuilder();
            courses.stream().forEach(subjectCurriculum -> {
                subCurrStr.append(String.format("%s,%s", subjectCurriculum.getSubject().getSubjectName(), subjectCurriculum.getCurriculum().getCurriculumName()));
            });

            user.setSubCurrStr(subCurrStr.toString());
        }
        user.setUuid(UUID.randomUUID().toString());
        user.setRole(Optional.ofNullable(role.toUpperCase()).orElse("STUDENT"));

        repository.save(user);

        // TODO - Cognito stuff

    }

    private org.springframework.security.core.userdetails.User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        return principal;
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

