package com.interjoin.teach.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder(toBuilder = true)
public class AffiliateMarketerDto {

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    @Email
    private String email;
    @NotNull
    private String phoneNumber;
    @NotNull
    private String referalCode;
}
