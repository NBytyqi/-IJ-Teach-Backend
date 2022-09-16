package com.interjoin.teach.repositories;

import com.interjoin.teach.dtos.interfaces.UserInterface;
import com.interjoin.teach.entities.User;
import com.interjoin.teach.enums.JoinAgencyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByCognitoUsername(String username);
    Optional<User> findByUuid(String uuid);
    Optional<User> findByEmail(String email);

    Optional<User> findByRoleAndId(String role, Long id);

    // GET AGENCY TEACHER
    List<User> findByAgencyAndAgencyNameAndJoinAgencyStatus(boolean isAgency, String agencyName, JoinAgencyStatus status);

    List<User> findByAgency(boolean isAgency);

    Optional<User> findFirstByAgencyCode(String agencyCode);

    @Query(value = "SELECT u.id, u.first_name as firstName, u.last_name as lastName, u.short_bio as shortBio, u.listed_price as listedPrice, u.rating, u.agency_name as agencyName, u.qualifications, u.long_bio as longBio, u.location as location, u.verified_teacher as verifiedTeacher, u.subjects_str as subjectsStr, u.curriculums_str as curriculumsStr, u.aws_profile_picture_url as awsProfilePictureUrl  from users u " +
            " inner join user_curriculum_subject ucs ON u.id = ucs.user_id WHERE ucs.subject_id = :subjectId AND u.role='TEACHER'",
    nativeQuery = true)
    List<UserInterface> getTeachersPerSubject(@Param("subjectId") Long subjectId);

    @Query(value = "SELECT u.id, u.first_name as firstName, u.last_name as lastName, u.short_bio as shortBio, u.listed_price as listedPrice, u.rating, u.agency_name as agencyName, u.qualifications, u.long_bio as longBio, u.location as location, u.verified_teacher as verifiedTeacher, u.subjects_str as subjectsStr, u.curriculums_str as curriculumsStr, u.aws_profile_picture_url as awsProfilePictureUrl from users u " +
            " inner join user_curriculum_subject ucs ON u.id = ucs.user_id WHERE ucs.subject_id = :subjectId AND ucs.curriculum_Id = :curriculumId AND u.role='TEACHER'",
            nativeQuery = true)
    List<UserInterface> getTeachersPerSubjectAndCurriculum(@Param("subjectId") Long subjectId, @Param("curriculumId") Long curriculumId);

    List<User> findByAwsProfilePictureRefIsNotNull();


    @Query("SELECT agency.awsProfilePictureUrl from User as agency where agency.agency = true AND agency.agencyName=:agencyName")
    String getAgencyProfilePicture(@Param("agencyName") String agencyName);

}
