package com.interjoin.teach.entities;

import com.interjoin.teach.embeddable.SubjectCurriculumKey;
import lombok.Getter;

import javax.persistence.*;


@Entity(name = "subject_curriculum")
@Getter
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
