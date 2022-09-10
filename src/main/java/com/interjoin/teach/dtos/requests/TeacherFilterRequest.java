package com.interjoin.teach.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TeacherFilterRequest {

    private String curriculum;
    private List<String> subjects;
    private Boolean verifiedTeacher;
}
