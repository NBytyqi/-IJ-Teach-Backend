package com.interjoin.teach.dtos.requests;

import com.interjoin.teach.dtos.SubjectCurriculumDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class UpdateProfileRequest {
    private String password;
    private String location;
    private String phone;

    private Set<SubjectCurriculumDto> subCurrList;


}
