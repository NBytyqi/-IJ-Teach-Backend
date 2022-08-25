package com.interjoin.teach.entities;

import com.interjoin.teach.embeddable.SubjectCurriculumKey;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity(name = "subject_curriculum")
@Getter
@Setter
@NoArgsConstructor
public class SubjectCurriculum {

    @EmbeddedId
    private SubjectCurriculumKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subject_id")
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("curriculum_id")
    @JoinColumn(name = "curriculum_id")
    private Curriculum curriculum;
}
