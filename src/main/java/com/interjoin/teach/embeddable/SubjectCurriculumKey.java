package com.interjoin.teach.embeddable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SubjectCurriculumKey implements Serializable {

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "curriculum_id")
    private Long curriculumId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubjectCurriculumKey)) return false;
        SubjectCurriculumKey that = (SubjectCurriculumKey) o;
        return subjectId.equals(that.subjectId) && curriculumId.equals(that.curriculumId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectId, curriculumId);
    }
}
