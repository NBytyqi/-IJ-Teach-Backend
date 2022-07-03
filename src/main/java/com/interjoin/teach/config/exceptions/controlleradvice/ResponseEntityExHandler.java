package com.interjoin.teach.config.exceptions.controlleradvice;

import com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException;
import com.amazonaws.services.cognitoidp.model.CodeMismatchException;
import com.interjoin.teach.config.exceptions.EmailAlreadyExistsException;
import com.interjoin.teach.config.exceptions.ExceptionMessage;
import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.config.exceptions.ReviewSessionException;
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

    @ExceptionHandler(value =
            InterjoinException.class)
    protected ResponseEntity<Object> handleConflict(InterjoinException ex, WebRequest request) {
        String bodyOfException = ex.getMessage();
        ExceptionMessage exceptionMessage = new ExceptionMessage(ex.getStatus(), bodyOfException, ex.getLocalizedMessage());
        return new ResponseEntity<Object>(exceptionMessage, exceptionMessage.getHttpStatus());
    }

    @ExceptionHandler(value =
            EmailAlreadyExistsException.class)
    protected ResponseEntity<Object> handleConflict(EmailAlreadyExistsException ex, WebRequest request) {
        String bodyOfException = "Email already in use";
        ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.BAD_REQUEST, bodyOfException, ex.getLocalizedMessage());
        return new ResponseEntity<Object>(exceptionMessage, exceptionMessage.getHttpStatus());
    }

    @ExceptionHandler(value =
            CodeMismatchException.class)
    protected ResponseEntity<Object> handleConflict(CodeMismatchException ex, WebRequest request) {
        String bodyOfException = "OTP code is not correct";
        ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.BAD_REQUEST, bodyOfException, ex.getLocalizedMessage());
        return new ResponseEntity<Object>(exceptionMessage, exceptionMessage.getHttpStatus());
    }

    // TODO
    @ExceptionHandler(value =
            ReviewSessionException.class)
    protected ResponseEntity<Object> handleConflict(ReviewSessionException ex, WebRequest request) {
        String bodyOfException = "You are not reviewing your session";
        ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.BAD_REQUEST, bodyOfException, ex.getLocalizedMessage());
        return new ResponseEntity<Object>(exceptionMessage, exceptionMessage.getHttpStatus());
    }
}
