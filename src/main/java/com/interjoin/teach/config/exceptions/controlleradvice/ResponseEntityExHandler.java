package com.interjoin.teach.config.exceptions.controlleradvice;


import com.amazonaws.services.cognitoidp.model.*;
import com.interjoin.teach.config.exceptions.EmailAlreadyExistsException;
import com.interjoin.teach.config.exceptions.ExceptionMessage;
import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.config.exceptions.ReviewSessionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ResponseEntityExHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value =
            InterjoinException.class)
    protected ResponseEntity<Object> handleConflict(InterjoinException ex, WebRequest request) {
        String bodyOfException = ex.getMessage();
        ExceptionMessage exceptionMessage = new ExceptionMessage(ex.getStatus(), bodyOfException);
        return new ResponseEntity<>(exceptionMessage, exceptionMessage.getHttpStatus());
    }

    @ExceptionHandler(value =
            EmailAlreadyExistsException.class)
    protected ResponseEntity<Object> handleConflict(EmailAlreadyExistsException ex, WebRequest request) {
        String bodyOfException = "Email already in use";
        ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.BAD_REQUEST, bodyOfException);
        return new ResponseEntity<>(exceptionMessage, exceptionMessage.getHttpStatus());
    }

    @ExceptionHandler(value =
            UserNotConfirmedException.class)
    protected ResponseEntity<Object> handleConflict(UserNotConfirmedException ex, WebRequest request) {
        String bodyOfException = "User is not confirmed";
        ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.FORBIDDEN, bodyOfException);
        return new ResponseEntity<>(exceptionMessage, exceptionMessage.getHttpStatus());
    }

    @ExceptionHandler(value =
            InternalErrorException.class)
    protected ResponseEntity<Object> handleConflict(InternalErrorException ex, WebRequest request) {
        String bodyOfException = "Internal server error";
        ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.FORBIDDEN, bodyOfException);
        return new ResponseEntity<>(exceptionMessage, exceptionMessage.getHttpStatus());
    }

    @ExceptionHandler(value =
            InvalidPasswordException.class)
    protected ResponseEntity<Object> handleConflict(InvalidPasswordException ex, WebRequest request) {
        String bodyOfException = "Invalid password";
        ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.FORBIDDEN, bodyOfException);
        return new ResponseEntity<>(exceptionMessage, exceptionMessage.getHttpStatus());
    }

    @ExceptionHandler(value =
            InvalidParameterException.class)
    protected ResponseEntity<Object> handleConflict(InvalidParameterException ex, WebRequest request) {
        String bodyOfException = "Invalid parameter: " + ex.getMessage();
        ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.FORBIDDEN, bodyOfException);
        return new ResponseEntity<>(exceptionMessage, exceptionMessage.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {

        List<String> validationErrors = (List) exception.getBindingResult().getFieldErrors().stream().map((fe) -> {
            return  fe.getDefaultMessage();
        }).collect(Collectors.toList());
        ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.BAD_REQUEST, "Fields are not valid", validationErrors);
        return new ResponseEntity<>(exceptionMessage, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(value =
            UsernameExistsException.class)
    protected ResponseEntity<Object> handleConflict(UsernameExistsException ex, WebRequest request) {
        String bodyOfException = ex.getErrorMessage();
        ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.UNAUTHORIZED, bodyOfException);
        return new ResponseEntity<>(exceptionMessage, exceptionMessage.getHttpStatus());
    }

    @ExceptionHandler(value =
            NotAuthorizedException.class)
    protected ResponseEntity<Object> handleConflict(NotAuthorizedException ex, WebRequest request) {
        String bodyOfException = "Email or password incorrect";
        ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.UNAUTHORIZED, bodyOfException);
        return new ResponseEntity<>(exceptionMessage, exceptionMessage.getHttpStatus());
    }


    @ExceptionHandler(value =
            UserNotFoundException.class)
    protected ResponseEntity<Object> handleConflict(UserNotFoundException ex, WebRequest request) {
        String bodyOfException = "Email or password incorrect";
        ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.UNAUTHORIZED, bodyOfException);
        return new ResponseEntity<>(exceptionMessage, exceptionMessage.getHttpStatus());
    }

    @ExceptionHandler(value =
            AccessDeniedException.class)
    protected ResponseEntity<Object> handleConflict(AccessDeniedException ex, WebRequest request) {
        String bodyOfException = "Access denied!";
        ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.FORBIDDEN, bodyOfException);
        return new ResponseEntity<>(exceptionMessage, exceptionMessage.getHttpStatus());
    }

    @ExceptionHandler(value =
            ReviewSessionException.class)
    protected ResponseEntity<Object> handleConflict(ReviewSessionException ex, WebRequest request) {
        String bodyOfException = "You are not reviewing your session";
        ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.BAD_REQUEST, bodyOfException);
        return new ResponseEntity<>(exceptionMessage, exceptionMessage.getHttpStatus());
    }

    @ExceptionHandler(value =
            Exception.class)
    protected ResponseEntity<Object> handleConflict(Exception ex, WebRequest request) {
        String bodyOfException = "An error occured. Please contact support.";
        ExceptionMessage exceptionMessage = new ExceptionMessage(HttpStatus.INTERNAL_SERVER_ERROR, bodyOfException);
        return new ResponseEntity<>(exceptionMessage, exceptionMessage.getHttpStatus());
    }
}