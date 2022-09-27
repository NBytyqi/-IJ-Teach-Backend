package com.interjoin.teach.dtos.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LogoutRequest {

//    @NotNull
    private String refreshToken;
    private String token;
}
