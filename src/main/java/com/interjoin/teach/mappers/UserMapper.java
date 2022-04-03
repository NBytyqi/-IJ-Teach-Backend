package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.UserDto;
import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.entities.User;

import java.util.Arrays;

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
                .shortBio(request.getShortBio())
//                .subjectCurriculums(request.getSubCurrList())
                .qualifications(request.getQualifications())
                .experience(request.getExperience())
                .timeZone(request.getTimeZone())
                .build();
    }

    public static UserDto map(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .location(user.getLocation())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .profilePicture(user.getProfilePicture())
                .parentEmail(user.getParentEmail())
                .shortBio(user.getShortBio())
                .longBio(user.getLongBio())
//                .profilePicture(Arrays.copyOf( user.getProfilePicture(), user.getProfilePicture().length ))
                .build();
    }
}
