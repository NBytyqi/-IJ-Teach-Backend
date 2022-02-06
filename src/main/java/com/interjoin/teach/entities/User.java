package com.interjoin.teach.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private byte[] profilePicture;

    @Column(name = "parent_email")
    @Email
    private String parentEmail;

    // TODO - ADD SUBJECTS AND CURRICULUMS

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "student_curriculum_subject",
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
    private String username;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

}
