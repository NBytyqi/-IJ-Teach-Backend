package com.interjoin.teach.repositories;

import com.interjoin.teach.dtos.TeacherInfo;
import com.interjoin.teach.dtos.interfaces.UserInterface;
import com.interjoin.teach.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    Optional<User> findByCognitoUsername(String username);
    Optional<User> findByUuid(String uuid);
    Optional<User> findByEmail(String email);

    // GET AGENCY USERS
    Page<User> findByAgencyAndAgencyName(boolean isAgency, String agencyName, Pageable pageable);

    Optional<User> findFirstByAgencyCode(String agencyCode);

    @Query(value = "SELECT u.id, u.first_name as firstName, u.last_name as lastName, u.short_bio as shortBio, u.listed_price as listedPrice, u.rating, u.agency_name as agencyName, u.qualifications, u.long_bio as longBio, u.location as location from users u " +
            " inner join user_curriculum_subject ucs ON u.id = ucs.user_id WHERE ucs.subject_id = :subjectId AND u.role='TEACHER'",
    nativeQuery = true)
    List<UserInterface> getTeachersPerSubject(@Param("subjectId") Long subjectId);



}
