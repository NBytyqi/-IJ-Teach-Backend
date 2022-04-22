package com.interjoin.teach.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ResetPasswordDTO {
    @Email(message = "Email should not be empty")
    private String email;
    @NotNull(message = "Verification code should not be empty")
    private String code;
    @NotNull(message = "New password can not be empty")
    private String newPassword;
}