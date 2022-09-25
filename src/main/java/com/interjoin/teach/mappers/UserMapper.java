package com.interjoin.teach.mappers;

import com.interjoin.teach.dtos.AgencyTeacher;
import com.interjoin.teach.dtos.TeacherDto;
import com.interjoin.teach.dtos.UserDto;
import com.interjoin.teach.dtos.UserSignupRequest;
import com.interjoin.teach.dtos.interfaces.UserInterface;
import com.interjoin.teach.entities.User;

import java.util.*;

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
                .purchasedVerification(false)
//                .subjectCurriculums(request.getSubCurrList())
                .qualifications(request.getQualifications())
//                .experience(request.getExperience())
                .timeZone(request.getTimeZone())
                .build();
    }

    public static UserDto map(User user) {
        return UserDto.builder()
                .id(user.getId())
                .uuid(user.getUuid())
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
                .purchasedVerification(user.getPurchasedVerification())
//                .profilePicture(user.getProfilePicture())
                .subCurrList(Optional.ofNullable(user.getSubjectCurriculums()).map(SubjectCurriculumMapper::map).orElse(null))
                .experiences(Optional.ofNullable(user.getExperiences()).map(ExperienceMapper::mapList).orElse(null))
                .pricePerHour(user.getPricePerHour())
                .listedPrice(user.getListedPrice())
                .agencyName(user.getAgencyName())
                .verifiedTeacher(user.isVerifiedTeacher())
                .timezone(user.getTimeZone())
                .role(user.getRole())
                .subjects(user.getSubjects())
                .favoriteTeacherIds(user.getFavoriteTeacherIds())
                .awsProfilePictureUrl(user.getAwsProfilePictureUrl())
                .build();
    }

    public static TeacherDto mapTeacher(User user) {
        return TeacherDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .uuid(user.getUuid())
                .awsProfilePictureUrl(user.getAwsProfilePictureUrl())
//                .profilePicture(user.getProfilePicture())
                .curriculumsStr(user.getAwsProfilePictureUrl())
                .build();
    }

    public static TeacherDto mapTeacher(UserInterface user) {
        return TeacherDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .agencyName(user.getAgencyName())
                .qualifications(user.getQualifications())
                .listedPrice(user.getListedPrice())
                .longBio(user.getLongBio())
                .shortBio(user.getShortBio())
                .location(user.getLocation())
                .rating(user.getRating())
//                .profilePicture(user.getProfilePicture())
                .awsProfilePictureUrl(user.getAwsProfilePictureUrl())
                .verifiedTeacher(user.getVerifiedTeacher())
                .subjectsStr(user.getSubjectsStr())
                .curriculumsStr(user.getCurriculumsStr())
                .build();
    }

    public static List<TeacherDto> mapTeachers(List<UserInterface> users) {
        List<TeacherDto> teachers = new ArrayList<>();
        for(UserInterface user : users) {
            teachers.add(mapTeacher(user));
        }
        return teachers;
    }

    public static List<AgencyTeacher> mapAgencyTeachers(List<User> users) {
        List<AgencyTeacher> agencyTeachers = new ArrayList<>();

        for(User user : users) {
            agencyTeachers.add(mapAgencyTeacher(user));
        }
        return agencyTeachers;
    }

    private static AgencyTeacher mapAgencyTeacher(User user) {
        return AgencyTeacher.builder()
                .id(user.getId())
                .dateOfJoiningAgency(user.getDateOfJoiningAgency())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .listedPrice(user.getListedPrice())
                .location(user.getLocation())
                .longBio(user.getLongBio())
                .rating(user.getRating())
                .shortBio(user.getShortBio())
                .subCurrList(Optional.ofNullable(user.getSubjectCurriculums()).map(SubjectCurriculumMapper::map).orElse(new HashSet<>()))
                .totalEarnings(user.getTotalEarned())
                .totalHours(user.getTotalHours())
                .awsProfilePictureUrl(user.getAwsProfilePictureUrl())
//                .profilePicture(user.getProfilePicture())
                .build();
    }
}
