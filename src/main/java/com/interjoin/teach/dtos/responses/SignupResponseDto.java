package com.interjoin.teach.dtos.responses;

import com.interjoin.teach.dtos.SubjectCurriculumDto;
import com.interjoin.teach.dtos.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SignupResponseDto {
    private String firstName;
    private String lastName;
    private String cognitoUsername;
    private String uuid;
    private Set<SubjectCurriculumDto> subCurrList;
    private UserDto user;
}
