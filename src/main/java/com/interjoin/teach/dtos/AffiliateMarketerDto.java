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

    @NotNull(message = "First name cannot be empty")
    private String firstName;
    @NotNull(message = "Last name cannot be empty")
    private String lastName;

    @NotNull(message = "Email cannot be empty")
    @Email
    private String email;
    @NotNull(message = "Phone number cannot be empty")
    private String phoneNumber;

    private String referalCode;
}
