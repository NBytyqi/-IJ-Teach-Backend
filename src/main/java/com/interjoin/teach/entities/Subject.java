package com.interjoin.teach.entities;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "curriculums")
public class Subject {

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

    @Column(name = "subject_name", nullable = false, unique = true)
    private String subjectName;

//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "subject_curriculum",
//            joinColumns = @JoinColumn(name = "subject_id"),
//            inverseJoinColumns = @JoinColumn(name = "curriculum_id")
//    )

//    @OneToMany(mappedBy = "subject")
//    private Set<SubjectCurriculum> subCurr;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "subjects")
    private Set<Curriculum> curriculums;


}
