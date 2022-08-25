package com.interjoin.teach.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactUsRequest {

    private String name;
    private String email;
    private String message;
}
