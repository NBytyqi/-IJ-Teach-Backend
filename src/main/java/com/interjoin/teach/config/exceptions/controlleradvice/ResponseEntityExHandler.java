package com.interjoin.teach.config.exceptions.controlleradvice;

import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.interjoin.teach.config.exceptions.ExceptionMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ResponseEntityExHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value =
            AWSCognitoIdentityProviderException.class)
    protected ResponseEntity<Object> handleConflict(AWSCognitoIdentityProviderException ex, WebRequest request) {
        String bodyOfException = ex.getErrorCode();
        ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.BAD_REQUEST, bodyOfException, ex.getLocalizedMessage());
        return new ResponseEntity<Object>(exceptionMessage, exceptionMessage.getHttpStatus());
    }
}
