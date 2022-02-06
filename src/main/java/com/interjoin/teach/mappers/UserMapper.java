package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.entities.User;

public class UserMapper {

    public static User mapUserRequest(UserSignupRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .email(request.getEmail())
                .location(request.getLocation())
                .longBio(request.getLongBio())
                .parentEmail(request.getParentEmail())
                .phoneNumber(request.getPhoneNumber())
                .profilePicture(request.getProfilePicture())
                .shortBio(request.getShortBio())
                .subjectCurriculums(request.getSubCurrList())
                .build();
    }
}
