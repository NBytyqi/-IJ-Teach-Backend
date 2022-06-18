package com.interjoin.teach.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "uuid", updatable = false)
    private String uuid;

    @JsonIgnore
    private String cognitoUsername;

    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Email
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "location")
    private String location;

    private String phoneNumber;
    private LocalDate dateOfBirth;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "profile_picture")
    private byte[] profilePicture;

    @Column(name = "parent_email")
    @Email
    private String parentEmail;

    // TODO - ADD SUBJECTS AND CURRICULUMS

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_curriculum_subject",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = { @JoinColumn(name = "subject_id", referencedColumnName = "subject_id"),
                                    @JoinColumn(name = "curriculum_id", referencedColumnName = "curriculum_id")}

    )
    private Set<SubjectCurriculum> subjectCurriculums;

    // This will be used for faster search of teachers for sub curriculum
    @Column(name = "sub_curr_str")
    private String subCurrStr;

    // Teacher specific
    @Column(name = "short_bio")
    private String shortBio;

    @Column(name = "long_bio")
    private String longBio;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "username")
    @JsonIgnore
    private String username;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @OneToMany(fetch = FetchType.LAZY)
    private List<AvailableTimes> availableTimes;

    @OneToMany(mappedBy = "user")
    private List<Experience> experiences = new java.util.ArrayList<>();

    private String timeZone;

    private BigDecimal pricePerHour;

    private String qualifications;
    private String experience;

    // IF AGENCY
    @Column(nullable = true)
    private boolean agency;
    @Column(nullable = true)
    private String agencyName;
    @Column(nullable = true)
    private String agencyCode;
//    private String location;
    @Column(nullable = true)
    private String additionalComments;
    @Column(nullable = true)
    private Integer numberOfTeachers;

    private Long previousSuccessfulSessions;

    private Double rating;
    @Column(nullable = true)
    private boolean verifiedTeacher;



}
