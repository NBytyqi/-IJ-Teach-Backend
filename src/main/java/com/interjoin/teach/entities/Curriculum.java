package com.interjoin.teach.entities;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "subjects")
public class Curriculum {

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

    @Column(name = "curriculum_name", nullable = false)
    private String curriculumName;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "subject_curriculum",
    joinColumns = @JoinColumn(name = "curriculum_id"),
    inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<Subject> subjects;
}
