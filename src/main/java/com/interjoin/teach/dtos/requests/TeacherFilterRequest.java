package com.interjoin.teach.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherFilterRequest {

    private String curriculum;
    private List<String> subjects;
}
