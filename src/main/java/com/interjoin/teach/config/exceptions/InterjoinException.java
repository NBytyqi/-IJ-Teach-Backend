package com.interjoin.teach.config.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InterjoinException extends Exception {

    private String message;
    private HttpStatus status;

}
