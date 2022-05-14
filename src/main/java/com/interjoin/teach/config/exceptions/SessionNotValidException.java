package com.interjoin.teach.config.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SessionNotValidException extends Exception {

    public SessionNotValidException(String message) {
        super(message);
    }
}
