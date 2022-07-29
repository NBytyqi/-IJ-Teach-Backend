package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.TeacherDto;
import com.interjoin.teach.dtos.UserDto;
import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.dtos.interfaces.UserInterface;
import com.interjoin.teach.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
//                .experience(request.getExperience())
                .timeZone(request.getTimeZone())
                .build();
    }

    public static UserDto map(User user) {
        return UserDto.builder()
                .id(user.getId())
                .rating(user.getRating())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .location(user.getLocation())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .parentEmail(user.getParentEmail())
                .shortBio(user.getShortBio())
                .previousSuccessfulSessions(user.getPreviousSuccessfulSessions())
                .longBio(user.getLongBio())
                .qualifications(user.getQualifications())
                .profilePicture(
                        Optional.ofNullable(user.getProfilePicture()).map(prf ->
                        Arrays.copyOf( user.getProfilePicture(), user.getProfilePicture().length ))
                                .orElse(null))
                .subCurrList(Optional.ofNullable(user.getSubjectCurriculums()).map(SubjectCurriculumMapper::map).orElse(null))
                .experiences(Optional.ofNullable(user.getExperiences()).map(ExperienceMapper::mapList).orElse(null))
                .pricePerHour(user.getPricePerHour())
                .listedPrice(user.getListedPrice())
                .agencyName(user.getAgencyName())
                .verifiedTeacher(user.isVerifiedTeacher())
                .verifiedEmail(user.isVerifiedEmail())
                .timezone(user.getTimeZone())
                .role(user.getRole())
                .subjects(user.getSubjects())
                .favoriteTeacherIds(user.getFavoriteTeacherIds())
                .build();
    }

    public static TeacherDto mapTeacher(User user) {
        return TeacherDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
//                .profilePicture()
                .build();
    }

    public static TeacherDto mapTeacher(UserInterface user) {
        return TeacherDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .agencyName(user.getAgencyName())
                .qualifications(user.getQualifications())
                .listedPrice(user.getListedPrice())
                .longBio(user.getLongBio())
                .shortBio(user.getShortBio())
                .location(user.getLocation())
                .rating(user.getRating())
//                .profilePicture()
                .build();
    }

    public static List<TeacherDto> mapTeachers(List<UserInterface> users) {
        List<TeacherDto> teachers = new ArrayList<>();
        for(UserInterface user : users) {
            teachers.add(mapTeacher(user));
        }
        return teachers;
    }

}
