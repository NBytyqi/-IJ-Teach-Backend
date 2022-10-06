package com.interjoin.teach.dtos.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RefreshTokenLoginRequest {

    @NotNull
    private String refreshToken;
    @NotNull
    private String cognitoUsername;
}
