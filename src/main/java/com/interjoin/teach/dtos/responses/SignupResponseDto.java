package com.interjoin.teach.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SignupResponseDto {
    private String firstName;
    private String lastName;
    private String cognitoUsername;
}
